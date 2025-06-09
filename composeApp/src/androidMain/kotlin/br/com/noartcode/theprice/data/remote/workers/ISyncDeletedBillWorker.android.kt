package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val SYNC_DELETED_BILL_INPUT_KEY = "sync_deleted_bill"

actual class SyncDeletedBillWorker(
    private val manager: WorkManager,
) : ISyncDeletedBillWorker {
    override fun sync(billID: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncDeletedBillWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(SYNC_DELETED_BILL_INPUT_KEY to billID))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        manager.enqueueUniqueWork(billID, ExistingWorkPolicy.KEEP, workRequest)
    }
}

class AndroidSyncDeletedBillWorker(
    appContext: Context,
    params: WorkerParameters,
    private val remoteDataSource: BillRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val billId = inputData
            .getString(SYNC_DELETED_BILL_INPUT_KEY)
            ?: return@withContext Result.failure(workDataOf("error_message" to "No Bill ID found"))


        return@withContext when(val result = remoteDataSource.delete(billId)) {
            is Resource.Success -> { Result.success() }
            is Resource.Error -> {
                result.exception?.printStackTrace()
                Result.retry()
            }
            is Resource.Loading -> Result.failure(workDataOf("error_message" to "Invalid response"))
        }
    }
}