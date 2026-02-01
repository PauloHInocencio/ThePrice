package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.isValid
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface IInsertMissingPayments {
    suspend operator fun invoke(date: DayMonthAndYear) : List<Payment>
}

class InsertMissingPayments(
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val getTodayDate: IGetTodayDate,
    private val dispatcher: CoroutineDispatcher,
) : IInsertMissingPayments {

    override suspend fun invoke(date: DayMonthAndYear) : List<Payment> = withContext(dispatcher) {

        val bills = billsRepository.getBillsBy(Bill.Status.ACTIVE)
            .first()
            .filter { bill ->
                bill.billingStartDate < date || (bill.billingStartDate.month == date.month && bill.billingStartDate.year == date.year)
            }

        val existingPayments = paymentsRepository
            .getMonthPayments(month = date.month, year = date.year)
            .first()
            .associateBy { it.billId }

        val paymentsToAdd = mutableListOf<Payment>()
        val timeStamp = getTodayDate().toEpochMilliseconds()
        for (bill in bills) {
            if (existingPayments.containsKey(bill.id).not()) {

                // TODO: Search for a better way to find a valid due date
                var dueDate:DayMonthAndYear = date.copy(day = bill.billingStartDate.day)
                while(dueDate.isValid().not()) {
                    dueDate = dueDate.copy(day = dueDate.day - 1)
                }

                paymentsToAdd.add(
                    Payment(
                        billId = bill.id,
                        dueDate = dueDate,
                        price = bill.price,
                        isPayed = false,
                        createdAt = timeStamp,
                        updatedAt = timeStamp,
                        isSynced = false,
                    )
                )
            }
        }

        return@withContext paymentsRepository.insert(paymentsToAdd)
    }

}
