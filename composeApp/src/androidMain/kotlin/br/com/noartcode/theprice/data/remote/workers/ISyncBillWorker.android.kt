package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val SYNC_BILL_WORK_INPUT_KEY = "bill_id"

actual class SyncBillWorker(
    private val manager:WorkManager,
): ISyncBillWorker {
    override fun sync(billID:String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncBillWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(SYNC_BILL_WORK_INPUT_KEY to billID))
            .build()
        manager.enqueueUniqueWork(billID, ExistingWorkPolicy.KEEP, workRequest)
    }
}


class AndroidSyncBillWorker(
    private val appContext:Context,
    private val params: WorkerParameters,
    private val billsRepository:BillsRepository,
    private val ioDispatcher: CoroutineDispatcher,
)  : CoroutineWorker(appContext, params){

    override suspend fun doWork(): Result = withContext(ioDispatcher){

        val billID = inputData
            .getString(SYNC_BILL_WORK_INPUT_KEY)
            ?: return@withContext Result.failure(workDataOf("error_message" to "No Bill ID founded"))

        val bill = billsRepository.get(billID)
            ?: return@withContext Result.failure(workDataOf("error_message" to "Bill not founded"))

        return@withContext when(val result = billsRepository.post(bill)) {
            is Resource.Error -> {
                result.exception?.printStackTrace()
                /*
                    Every work request has a backoff policy and backoff delay.
                    The default policy is EXPONENTIAL with a delay of 30 seconds,
                    but you can override this in your work request configuration.

                    source: https://developer.android.com/develop/background-work/background-tasks/persistent/getting-started/define-work#retries_backoff
                 */
                Result.retry()
            }
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



