package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.data.remote.workers.ISyncBillWorker
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


interface IInsertBill {
    suspend operator fun invoke(
        bill: Bill
    ) : Resource<String>
}

class InsertBill(
    private val repository: BillsRepository,
    private val worker: ISyncBillWorker, // TODO: work should be in viewmodel
    private val ioDispatcher: CoroutineDispatcher,
) : IInsertBill {

    override suspend fun invoke(
        bill: Bill
    ): Resource<String> = withContext(ioDispatcher) {
        try {
            val id = repository.insert(bill)
            //worker.sync(id)
            return@withContext Resource.Success(id)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "An error occurred when trying to add new bill"
            )
        }
    }
}