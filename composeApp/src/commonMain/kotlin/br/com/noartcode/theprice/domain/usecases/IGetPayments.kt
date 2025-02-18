package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


interface IGetPayments {
    operator fun invoke(
        date:DayMonthAndYear,
        billStatus: Bill.Status
    ) : Flow<Resource<List<Payment>>>
}


@OptIn(ExperimentalCoroutinesApi::class)
class GetPayments(
    private val billLDS: BillLocalDataSource,
    private val paymentLDS: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IGetPayments {

    override fun invoke(
        date: DayMonthAndYear,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> = combine(
        billLDS.getBillsBy(billStatus),
        paymentLDS.getMonthPayments(month = date.month, year = date.year)
    ) { activeBills, monthPayments -> activeBills to monthPayments }
        .flatMapLatest {  (activeBills, monthPayments) ->
            flow<Resource<List<Payment>>>{
                val monthPaymentsMap = monthPayments.associateBy { it.billId }
                val payments = activeBills.mapNotNull { bill ->
                    if (date < bill.billingStartDate) return@mapNotNull null
                    return@mapNotNull monthPaymentsMap[bill.id]!!
                }
                emit(Resource.Success(payments))
            }
        }
        .onStart { emit(Resource.Loading) }
        .catch { e ->
            if (e is CancellationException) throw e
            emit(Resource.Error(
                exception = e,
                message = "Something wrong happen when trying to get payments")
            )
        }
        .flowOn(dispatcher)

}
