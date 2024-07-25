package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSource
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
            val normalizedName = bill.name.trim().lowercase()
            val id = localDataSource.insert(
                name = normalizedName,
                description = bill.description,
                price = bill.price,
                type = bill.type.name,
                status = bill.status.name,
                invoiceDueDate = bill.invoiceDueDate
            )
            return@withContext Resource.Success(id)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "A Error occurred when trying to add new bill"
            )
        }
    }
}