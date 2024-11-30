package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


interface IGetPayments {
    operator fun invoke(
        date:DayMonthAndYear,
        billStatus: Bill.Status
    ) : Flow<Resource<List<Payment>>>
}

/*
class GetPayments(
    private val billLDS: BillLocalDataSource,
    private val paymentLDS: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IGetPayments {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(
        date: DayMonthAndYear,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> = billLDS
        .getBillsBy(billStatus)
        .flatMapLatest { bills ->
            flow {
                try {
                    val payments = bills.mapNotNull { bill ->
                        if (date < bill.billingStartDate) return@mapNotNull null
                        with(paymentLDS.getPayment(bill.id, date.month, date.year))  {
                            if (this != null) return@with this
                            val id = paymentLDS.insert(
                                billID = bill.id,
                                dueDate = DayMonthAndYear(
                                    day = bill.billingStartDate.day,
                                    month = date.month,
                                    year = date.year
                                )
                            )
                            return@with paymentLDS.getPayment(id)!!
                        }
                    }
                    emit(Resource.Success(payments))
                } catch (e:Throwable) {
                    emit(Resource.Error(
                        message = "Something wrong happen when trying to get payments",
                        exception = e
                    ))
                }
            }
        }.flowOn(dispatcher)
}
*/

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class GetPayments(
    private val billLDS: BillLocalDataSource,
    private val paymentLDS: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IGetPayments {

    private val mutex = Mutex()

    override fun invoke(
        date: DayMonthAndYear,
        billStatus: Bill.Status
    ): Flow<Resource<List<Payment>>> = combine(
        billLDS.getBillsBy(billStatus),
        paymentLDS.getMonthPayments(month = date.month, year = date.year)
    ) { activeBills, monthPayments -> activeBills to monthPayments }
        .flatMapLatest {  (activeBills, monthPayments) ->
            flowOf(processPayments(activeBills, monthPayments, date))
        }
        .debounce(10)
        .catch { e ->
            // Suppress CancellationException
            if (e !is CancellationException) {
                emit(Resource.Error(
                    message = "Something wrong happen when trying to get payments",
                    exception = e
                ))
            }
        }
        .flowOn(dispatcher)


    private suspend fun processPayments(
        activeBills: List<Bill>,
        monthPayments: List<Payment>,
        date: DayMonthAndYear
    ) : Resource<List<Payment>> {
        val monthPaymentsMap = monthPayments.associateBy { it.billId }
        val payments = activeBills.mapNotNull { bill ->
            if (date < bill.billingStartDate) return@mapNotNull null
            val currentPayment = monthPaymentsMap[bill.id]
            if (currentPayment != null) {
                return@mapNotNull currentPayment
            } else {
                mutex.withLock {
                    val paymentId = paymentLDS.insert(
                        billID = bill.id,
                        dueDate = DayMonthAndYear(
                            day = bill.billingStartDate.day,
                            month = date.month,
                            year = date.year
                        )
                    )
                    paymentLDS.getPayment(paymentId)
                }
            }
        }
        return Resource.Success(payments)
    }
}
