package br.com.noartcode.theprice.data.remote.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncUpdatedPaymentWorker
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import kotlinx.coroutines.CoroutineDispatcher

class SyncUpdatedPaymentWorkerTestFactory(
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return AndroidSyncUpdatedPaymentWorker(
            appContext = appContext,
            params = workerParameters,
            paymentsRepository = paymentsRepository,
            ioDispatcher = ioDispatcher
        )
    }
}