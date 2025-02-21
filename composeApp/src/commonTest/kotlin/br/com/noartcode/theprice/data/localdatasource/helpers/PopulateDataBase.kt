package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear

//TODO("Get rid of this function. Should be replaced by the InsertBillWithPayments use case")
internal suspend fun populateDBWithAnBillAndFewPayments(
    bill: Bill,
    billingStartDate: DayMonthAndYear,
    numOfPayments:Int,
    paymentsArePaid:Boolean = false,
    billDataSource: BillLocalDataSource,
    paymentDataSource: PaymentLocalDataSource,
) : Long {
    // Adding payments for a bill
    val billId = billDataSource.insert(
        bill = bill.copy(billingStartDate = billingStartDate)
    )
    val paidValue = bill.price
    with(paymentDataSource) {
        repeat(numOfPayments) { i ->
            val paymentMonth = billingStartDate.month + i
            insert(
                billID = billId,
                dueDate = billingStartDate.copy(month = paymentMonth),
                price = paidValue,
                isPayed = paymentsArePaid
            )
        }
    }

    return billId
}