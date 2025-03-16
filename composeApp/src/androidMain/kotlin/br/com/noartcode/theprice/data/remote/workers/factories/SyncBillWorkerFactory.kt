package br.com.noartcode.theprice.data.remote.workers.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncBillWorker
import br.com.noartcode.theprice.domain.repository.BillsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class SyncBillWorkerFactory(
    private val billRepository: BillsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return AndroidSyncBillWorker(
            appContext = appContext,
            params =  workerParameters,
            billRepository = billRepository,
            ioDispatcher = ioDispatcher,
        )
    }
}