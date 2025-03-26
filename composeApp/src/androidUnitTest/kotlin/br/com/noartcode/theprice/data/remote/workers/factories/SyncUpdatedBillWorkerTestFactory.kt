package br.com.noartcode.theprice.data.remote.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncUpdatedBillWorker
import br.com.noartcode.theprice.domain.repository.BillsRepository
import kotlinx.coroutines.CoroutineDispatcher

class SyncUpdatedBillWorkerTestFactory(
    private val billsRepository: BillsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return AndroidSyncUpdatedBillWorker(
            appContext = appContext,
            params = workerParameters,
            billsRepository = billsRepository,
            ioDispatcher = ioDispatcher,
        )
    }
}