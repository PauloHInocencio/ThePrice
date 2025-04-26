package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen


interface IGetPayments {
    operator fun invoke(
        date:DayMonthAndYear,
        billStatus: Bill.Status
    ) : Flow<Resource<List<Payment>>>
}


@OptIn(ExperimentalCoroutinesApi::class)
class GetPayments(
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val insertMissingPayments: IInsertMissingPayments,
    private val ioDispatcher: CoroutineDispatcher,
) : IGetPayments {

    override fun invoke(
        date: DayMonthAndYear,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> = combine(
        billsRepository.getBillsBy(billStatus),
        paymentsRepository.getMonthPayments(month = date.month, year = date.year)
    ) { activeBills, monthPayments -> activeBills to monthPayments }
        .flatMapLatest {  (activeBills, monthPayments) ->
            flow<Resource<List<Payment>>>{
                val monthPaymentsMap = monthPayments.associateBy { it.billId }
                val payments = activeBills.mapNotNull { bill ->
                    if (date < bill.billingStartDate) return@mapNotNull null
                    return@mapNotNull requireNotNull(monthPaymentsMap[bill.id])
                }
                emit(Resource.Success(payments))
            }
        }
        .onStart { emit(Resource.Loading) }
        .retryWhen { cause, attempt ->
            if(cause is IllegalArgumentException && attempt == 0L) {
                insertMissingPayments(date)
                return@retryWhen true
            }
            return@retryWhen false
        }
        .catch { e ->
            if (e is CancellationException) throw e
            emit(Resource.Error(
                exception = e,
                message = "Something wrong happen when trying to get payments")
            )
        }
        .flowOn(ioDispatcher)

}
