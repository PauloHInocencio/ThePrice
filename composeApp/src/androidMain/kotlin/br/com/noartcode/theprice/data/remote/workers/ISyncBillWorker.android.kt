package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val SYNC_BILL_WORK_INPUT_KEY = "bill_id"

actual class SyncBillWorker(
    private val manager:WorkManager,
): ISyncBillWorker {
    override fun sync(billID:String) {
        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncBillWorker>()
            .setInputData(workDataOf(SYNC_BILL_WORK_INPUT_KEY to billID))
            .build()
        manager.enqueueUniqueWork(billID, ExistingWorkPolicy.KEEP, workRequest)
    }
}


class AndroidSyncBillWorker(
    private val appContext:Context,
    private val params: WorkerParameters,
    private val billRepository:BillsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
)  : CoroutineWorker(appContext, params){

    override suspend fun doWork(): Result = withContext(ioDispatcher){
        val billID = inputData
            .getString(SYNC_BILL_WORK_INPUT_KEY)
            ?: return@withContext Result.failure(workDataOf("error_message" to "No Bill ID founded"))

        val bill = billRepository.getBill(billID)
            ?: return@withContext Result.failure(workDataOf("error_message" to "Bill not founded"))

        return@withContext when(val result = billRepository.post(bill)) {
            is Resource.Error -> Result.failure(workDataOf("error_message" to result.message))
            is Resource.Loading -> Result.failure(workDataOf("error_message" to "Invalid response"))
            is Resource.Success -> {
                try {
                    billRepository.update(bill.copy(isSynced = true))
                    Result.success()
                } catch (e:Throwable) {
                    Result.failure(workDataOf("error_message" to "Error when trying update bill"))
                }

            }
        }
    }

}



