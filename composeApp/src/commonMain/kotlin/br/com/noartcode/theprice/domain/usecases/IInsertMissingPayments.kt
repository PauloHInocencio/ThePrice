package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface IInsertMissingPayments {
    suspend operator fun invoke(date: DayMonthAndYear)
}

class InsertMissingPayments(
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val getTodayDate: IGetTodayDate,
    private val syncPaymentsWorker: ISyncPaymentsWorker,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IInsertMissingPayments {

    override suspend fun invoke(date: DayMonthAndYear) = withContext(dispatcher) {

        val bills = billsRepository.getBillsBy(Bill.Status.ACTIVE)
            .first()
            .filter {
                it.billingStartDate > date || (it.billingStartDate.month == date.month && it.billingStartDate.year == date.year)
            }

        val existingPayments = paymentsRepository
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

        paymentsRepository.insert(paymentsToAdd)
        syncPaymentsWorker()
    }

}
