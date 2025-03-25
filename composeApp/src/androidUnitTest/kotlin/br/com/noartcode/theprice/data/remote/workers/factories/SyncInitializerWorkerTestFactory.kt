package br.com.noartcode.theprice.data.remote.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncInitializerWorker
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import kotlinx.coroutines.CoroutineDispatcher

class SyncInitializerWorkerTestFactory(
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return AndroidSyncInitializerWorker(
            appContext = appContext,
            params = workerParameters,
            billsRepository = billsRepository,
            paymentsRepository = paymentsRepository,
            ioDispatcher = ioDispatcher,
        )
    }
}