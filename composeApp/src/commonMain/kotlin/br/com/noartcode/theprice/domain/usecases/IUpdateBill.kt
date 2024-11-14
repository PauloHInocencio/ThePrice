package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IUpdateBill {
    suspend operator fun invoke(bill: Bill) : Resource<Unit>
}

class UpdateBill(
    private val localDataSource: BillLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IUpdateBill {

    override suspend fun invoke(bill: Bill) = withContext(dispatcher) {
        try {
            return@withContext Resource.Success(localDataSource.update(bill))
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "A Error occurred when trying to update a bill"
            )
        }
    }
}