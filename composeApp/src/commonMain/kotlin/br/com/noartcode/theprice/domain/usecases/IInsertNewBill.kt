package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IInsertNewBill {
    suspend operator fun invoke(bill: Bill) : Resource<Long>
}

class InsertNewBill(
    private val localDataSource: BillLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IInsertNewBill {

    override suspend fun invoke(bill: Bill): Resource<Long> = withContext(dispatcher) {
        try {
            val id = localDataSource.insert(bill)
            return@withContext Resource.Success(id)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "A Error occurred when trying to add new bill"
            )
        }
    }
}