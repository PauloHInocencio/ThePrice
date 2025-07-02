package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedBillWorker
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IUpdateBill {
    suspend operator fun invoke(bill: Bill) : Resource<Unit>
}

class UpdateBill(
    private val repository: BillsRepository,
    private val dispatcher: CoroutineDispatcher,
) : IUpdateBill {

    override suspend fun invoke(bill: Bill) = withContext(dispatcher) {
        try {
            val result = Resource.Success(repository.update(bill))
            return@withContext result
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "A Error occurred when trying to update a bill"
            )
        }
    }
}