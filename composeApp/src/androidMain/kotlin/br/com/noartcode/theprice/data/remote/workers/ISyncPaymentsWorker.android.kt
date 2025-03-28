package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val SYNC_PAYMENTS_WORKER_NAME = "sync_payments"

actual class SyncPaymentsWorker(
    private val manager: WorkManager,
) : ISyncPaymentsWorker {
    override fun sync() {
        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncPaymentsWorker>()
            .build()
        manager.enqueueUniqueWork(SYNC_PAYMENTS_WORKER_NAME, ExistingWorkPolicy.REPLACE, workRequest)
    }
}

class AndroidSyncPaymentsWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {

        try {
            val paymentsToSync = paymentsRepository.getNotSynchronizedPayments()

            if (paymentsToSync.isEmpty()) {
                return@withContext Result.failure(workDataOf("error_message" to "No payments to sync"))
            }

            when(val result = paymentsRepository.post(paymentsToSync)) {
                is Resource.Success ->  {
                    paymentsRepository.update(paymentsToSync.map{ it.copy(isSynced = true) })
                    return@withContext Result.success()
                }
                is Resource.Error -> return@withContext Result.failure(workDataOf("error_message" to result.message))
                is Resource.Loading -> return@withContext Result.failure(workDataOf("error_message" to "Invalid response"))
            }
        } catch (e:Throwable) {
            e.printStackTrace()
            return@withContext Result.failure(workDataOf("error_message" to "Error while trying sync payments"))
        }

    }

}