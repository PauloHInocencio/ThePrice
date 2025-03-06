package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface IInsertMissingPayments {
    suspend operator fun invoke(date: DayMonthAndYear)
}

class InsertMissingPayments(
    private val billsLDS: BillLocalDataSource,
    private val paymentLDS: PaymentLocalDataSource,
    private val getTodayDate: IGetTodayDate,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IInsertMissingPayments {

    override suspend fun invoke(date: DayMonthAndYear) = withContext(dispatcher) {

        val bills = billsLDS.getBillsBy(Bill.Status.ACTIVE).first()
        val existingPayments = paymentLDS
            .getMonthPayments(month = date.month, year = date.year)
            .first()
            .associateBy { it.billId }

        val paymentsToAdd = mutableListOf<Payment>()
        val timeStamp = getTodayDate().toEpochMilliseconds()
        for (bill in bills) {
            if (existingPayments.containsKey(bill.id).not()) {
                paymentsToAdd.add(
                    Payment(
                        billId = bill.id,
                        dueDate = date.copy(day = bill.billingStartDate.day),
                        price = bill.price,
                        isPayed = false,
                        createdAt = timeStamp,
                        updatedAt = timeStamp,
                    )
                )
            }
        }

        paymentLDS.insert(paymentsToAdd)
    }

}
