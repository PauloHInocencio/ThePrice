package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.BillWithPayments
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.isTotalMonthsGreaterOrEqual
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


interface IUpdateBillWithPayments {
    suspend operator fun invoke(
        billWithPayments: BillWithPayments
    ) : Resource<BillWithPayments>
}

class UpdateBillWithPayments(
    private val billLocalDataSource: BillLocalDataSource,
    private val dispatcher: CoroutineDispatcher
) : IUpdateBillWithPayments {

    override suspend operator fun invoke(
        billWithPayments: BillWithPayments
    ) : Resource<BillWithPayments> = withContext(dispatcher) {
        try {
            val result = billLocalDataSource.updateBillWithPayments(
               bill = billWithPayments.bill,
               payments = billWithPayments.payments
           )
            return@withContext Resource.Success(result)
        } catch (e: Throwable) {
            return@withContext Resource.Error(
                exception = e,
                message = "An error occurred when trying to update bill with payments"
            )
        }
    }
}