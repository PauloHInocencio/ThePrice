package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


interface IInsertBill {
    suspend operator fun invoke(
        bill: Bill
    ) : Resource<String>
}

class InsertBill(
    private val localDataSource: BillLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IInsertBill {

    override suspend fun invoke(
        bill: Bill
    ): Resource<String> = withContext(dispatcher) {
        try {

            val id = localDataSource.insert(bill)

            return@withContext Resource.Success(id)

        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "An error occurred when trying to add new bill"
            )
        }
    }
}