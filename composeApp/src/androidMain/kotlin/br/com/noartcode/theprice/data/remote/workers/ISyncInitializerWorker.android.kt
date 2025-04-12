package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val SYNC_INITIALIZER_WORKER_NAME = "sync_initializer"

actual class SyncInitializerWorker(
    private val manager: WorkManager,
) : ISyncInitializerWorker {
    override fun invoke() {
        val workRequest = OneTimeWorkRequestBuilder<AndroidSyncInitializerWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        manager.enqueueUniqueWork(SYNC_INITIALIZER_WORKER_NAME, ExistingWorkPolicy.KEEP, workRequest)
    }
}

class AndroidSyncInitializerWorker(
    private val appContext: Context,
    private val params: WorkerParameters,
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        //TODO("Should Handle responses from the fetch calls. Try catch will never going to be executed.")
        try {
            billsRepository.fetchAllBills()
            paymentsRepository.fetchAllPayments()
            return@withContext Result.success()
        } catch (e: Throwable) {
            e.printStackTrace()
            return@withContext Result.retry()
        }
    }

}


