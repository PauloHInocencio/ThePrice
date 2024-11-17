package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IInsertOrReplaceBill {
    suspend operator fun invoke(
        name:String,
        description:String?,
        price:Int,
        type:Bill.Type,
        status:Bill.Status,
        billingStartDate: DayMonthAndYear,
    ) : Resource<Long>
}

class InsertOrReplaceBill(
    private val localDataSource: BillLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IInsertOrReplaceBill {

    override suspend fun invoke(
        name:String,
        description:String?,
        price:Int,
        type:Bill.Type,
        status:Bill.Status,
        billingStartDate: DayMonthAndYear,
    ): Resource<Long> = withContext(dispatcher) {
        try {
            val id = localDataSource.insert(
                name = name,
                description = description,
                price = price,
                type = type,
                status = status,
                billingStartDate = billingStartDate,
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