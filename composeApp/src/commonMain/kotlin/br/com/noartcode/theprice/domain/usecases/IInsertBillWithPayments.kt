package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toLocalDate
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.until

interface IInsertBillWithPayments {
    suspend operator fun invoke(
        bill: Bill,
        currentDate:DayMonthAndYear,
    ) : Resource<Long>
}

class InsertBillWithPayments(
    private val localDataSource: BillLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IInsertBillWithPayments {
    override suspend fun invoke(
        bill: Bill,
        currentDate: DayMonthAndYear
    ): Resource<Long> = withContext(dispatcher) {
        try {
            val start = bill.billingStartDate.toLocalDate()
            val numOfPayments = start.until(
                other = currentDate.toLocalDate(),
                unit = DateTimeUnit.MONTH
            ) + 1

            val payments = List(numOfPayments) { i ->
                Payment(
                    billId = 0L,
                    dueDate = start.plus(i, DateTimeUnit.MONTH).toDayMonthAndYear(),
                    price = bill.price,
                    isPayed = false
                )
            }

            val id = localDataSource.insertBillWithPayments(bill, payments)

            return@withContext Resource.Success(id)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "An error occurred when trying to add new bill with payments"
            )
        }
    }

}