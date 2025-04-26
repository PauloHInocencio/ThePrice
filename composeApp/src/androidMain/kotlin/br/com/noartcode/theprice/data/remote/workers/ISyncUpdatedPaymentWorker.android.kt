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
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

const val SYNC_UPDATED_PAYMENT_INPUT_KEY = "sync_updated_payment"

actual class SyncUpdatedPaymentWorker(
    private val manager: WorkManager,
) : ISyncUpdatedPaymentWorker {
    override fun sync(paymentID: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncUpdatedPaymentWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(SYNC_UPDATED_PAYMENT_INPUT_KEY to paymentID))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        manager.enqueueUniqueWork(paymentID, ExistingWorkPolicy.KEEP, workRequest)
    }
}

class AndroidSyncUpdatedPaymentWorker(
    appContext: Context,
    params: WorkerParameters,
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val paymentId = inputData
            .getString(SYNC_UPDATED_PAYMENT_INPUT_KEY)
            ?: return@withContext Result.failure(workDataOf("error_message" to "No Payment ID found"))

        val payment = paymentsRepository.get(paymentId)
            ?: return@withContext Result.failure(workDataOf("error_message" to "Payment not found"))

        return@withContext when(val result = paymentsRepository.put(payment)) {
            is Resource.Error -> {
                result.exception?.printStackTrace()
                Result.retry()
            }
            is Resource.Loading -> Result.failure(workDataOf("error_message" to "Invalid response"))
            is Resource.Success -> {
                try {
                    paymentsRepository.update(payment.copy(isSynced = true))
                    Result.success()
                } catch (e:Throwable) {
                   e.printStackTrace()
                    Result.failure(workDataOf("error_message" to "Error when trying update bill"))
                }
            }
        }
    }

}