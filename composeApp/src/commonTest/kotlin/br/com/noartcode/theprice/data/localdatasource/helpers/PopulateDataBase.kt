package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear

internal suspend fun populateDBWithAnBillAndFewPayments(
    bill: Bill, billingStartDate: DayMonthAndYear,
    numOfPayments:Int,
    paymentsArePaid:Boolean = false,
    billDataSource: BillLocalDataSource,
    paymentDataSource: PaymentLocalDataSource,
) : Long {
    // Adding payments for a bill
    val billId = billDataSource.insert(
        name = bill.name,
        description = bill.description,
        price = bill.price,
        status = bill.status,
        type = bill.type,
        billingStartDate = billingStartDate,
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