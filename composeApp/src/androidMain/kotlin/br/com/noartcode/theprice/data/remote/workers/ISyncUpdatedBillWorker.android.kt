package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val SYNC_UPDATED_BILL_INPUT_KEY = "sync_updated_bill"

actual class SyncUpdatedBillWorker(
    private val manager: WorkManager,
) : ISyncUpdatedBillWorker {
    override fun sync(billID: String) {
        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncUpdatedBillWorker>()
            .setInputData(workDataOf(SYNC_UPDATED_BILL_INPUT_KEY to billID))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        manager.enqueueUniqueWork(billID, ExistingWorkPolicy.KEEP, workRequest)
    }
}


class AndroidSyncUpdatedBillWorker(
    appContext:Context,
    params: WorkerParameters,
    private val billsRepository:BillsRepository,
    private val ioDispatcher:CoroutineDispatcher = Dispatchers.IO,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val billId = inputData
            .getString(SYNC_UPDATED_BILL_INPUT_KEY)
            ?: return@withContext Result.failure(workDataOf("error_message" to "No Bill ID found"))

        val bill = billsRepository.get(billId)
            ?: return@withContext Result.failure(workDataOf("error_message" to "Bill not found"))

        return@withContext when(val result = billsRepository.put(bill)) {
            is Resource.Error -> Result.failure(workDataOf("error_message" to result.message))
            is Resource.Loading -> Result.failure(workDataOf("error_message" to "Invalid response"))
            is Resource.Success -> {
                try {
                    billsRepository.update(bill.copy(isSynced = true))
                    Result.success()
                } catch (e:Throwable) {
                    e.printStackTrace()
                    Result.failure(workDataOf("error_message" to "Error when trying update bill"))
                }
            }
        }
    }
}


