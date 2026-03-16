package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.BillWithPayments
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


interface IUpdateBillWithPayments {
    suspend operator fun invoke(
        bill: Bill,
        startDateToApplyChanges: DayMonthAndYear
    ) : Resource<BillWithPayments>
}

class UpdateBillWithPayments(
    private val billLocalDataSource: BillLocalDataSource,
    private val paymentLocalDataSource: PaymentLocalDataSource,
    private val getTodayDate: IGetTodayDate,
    private val dispatcher: CoroutineDispatcher
) : IUpdateBillWithPayments {

    override suspend operator fun invoke(
        bill: Bill,
        startDateToApplyChanges: DayMonthAndYear
    ) : Resource<BillWithPayments> = withContext(dispatcher) {
        try {
            val paymentsToUpdate = paymentLocalDataSource
                .getBillPayments(bill.id)
                .filter { payment ->
                    payment.dueDate.isTotalMonthsGreaterOrEqual(startDateToApplyChanges)
                }
                .map { payment ->
                    // The changes propagated to payments are the payment Due Day and Price
                    payment.copy(
                        dueDate = payment.dueDate.copy(day = bill.billingStartDate.day),
                        price = bill.price,
                        updatedAt = getTodayDate().toEpochMilliseconds(),
                        isSynced = false
                    )
                }
            val result = billLocalDataSource.updateBillWithPayments(
               bill = bill.copy(
                   updatedAt = getTodayDate().toEpochMilliseconds(),
                   isSynced = false
               ),
               payments = paymentsToUpdate
           )

            return@withContext Resource.Success(result)
        } catch (e: Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "An error occurred when trying to update bill with payments"
            )
        }
    }

    private fun DayMonthAndYear.isTotalMonthsGreaterOrEqual(
        other: DayMonthAndYear
    ): Boolean {
        val totalMonths = this.year * 12 + this.month
        val otherTotalMonths = other.year * 12 + other.month
        return totalMonths >= otherTotalMonths
    }
}