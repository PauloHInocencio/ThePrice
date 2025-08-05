package br.com.noartcode.theprice.ui.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.local.mapper.toSyncEvent
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.EventDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.data.remote.mapper.toDomain
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.bill.IDeleteLocalBill
import br.com.noartcode.theprice.domain.usecases.IGetEvents
import br.com.noartcode.theprice.domain.usecases.payment.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.payment.IGetPayments
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBill
import br.com.noartcode.theprice.domain.usecases.bill.IInsertMissingPayments
import br.com.noartcode.theprice.domain.usecases.payment.IInsertPayments
import br.com.noartcode.theprice.domain.usecases.datetime.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBill
import br.com.noartcode.theprice.domain.usecases.payment.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.payment.IUpdatePaymentStatus
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi.Status.PAYED
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val moveMonth: IMoveMonth,
    getFirstPaymentDate: IGetOldestPaymentRecordDate,
    private val updatePaymentStatus: IUpdatePaymentStatus,
    private val getEvents: IGetEvents,
    private val updateBill: IUpdateBill,
    private val deleteBill: IDeleteLocalBill,
    private val insertNewBill: IInsertBill,
    private val updatePayment: IUpdatePayment,
    private val insertMissingPayments: IInsertMissingPayments,
    private val insertPayments: IInsertPayments,
    private val eventSyncQueue: EventSyncQueue,
): ViewModel() {


    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private val eventJob = getEvents().onEach { result ->
        result
            .doIfSuccess { eventStr: String ->
                println("receive event: $eventStr")
                val event = defaultJson.decodeFromString<EventDto>(eventStr)

                when (Pair(event.table, event.action)) {
                    ("bill" to "update") -> {
                        val bill = defaultJson.decodeFromString<BillDto>(event.data!!)
                        updateBill(bill.toDomain())
                    }
                    ("bill" to "create") -> {
                        val bill = defaultJson.decodeFromString<BillDto>(event.data!!)
                        insertNewBill(bill.toDomain())
                    }
                    ("bill" to "delete") -> {
                        val bill = defaultJson.decodeFromString<BillDto>(event.data!!)
                        deleteBill(bill.id)
                    }
                    ("payment" to "create") -> {
                        val payments = defaultJson.decodeFromString<List<PaymentDto>>(event.data!!)
                        insertPayments(payments.toDomain())
                    }
                    ("payment" to  "update") -> {
                        val payment = defaultJson.decodeFromString<PaymentDto>(event.data!!)
                        val paymentDomain = payment.toDomain()
                        updatePayment(paymentDomain)
                    }
                    else -> {
                        println("unknown event: $event")
                    }
                }
            }
            .doIfError {
                println("error: ${it.message}")
            }
    }.launchIn(viewModelScope)

    private val initialDate by lazy { getTodayDay() }
    private val _currentDate = MutableStateFlow(initialDate)
    private val _homeScreenResults =
        combine(_currentDate, getFirstPaymentDate()) { currentDate, firstPaymentDate ->
            currentDate to firstPaymentDate
        }
        .flatMapLatest { (currentDate, firstPaymentDate) ->
            getPayments(
                date = currentDate,
                billStatus = Bill.Status.ACTIVE
            ).retryWhen { cause, attempt ->
                if(cause is IllegalArgumentException && attempt == 0L) {
                    val payments = insertMissingPayments(currentDate)
                    eventSyncQueue.enqueue(payments.toSyncEvent("create"))
                    return@retryWhen true
                }
                return@retryWhen false
            }.catch { e ->
                if (e is CancellationException) throw e
                emit(Resource.Error(exception = e, message = "Something wrong happen when trying to get payments"))
            }.map { result ->
                result to (currentDate to firstPaymentDate)
            }
        }


    private val _state = MutableStateFlow(HomeUiState())
    val uiState = combine( _state, _homeScreenResults ) { state, (payments, dates) ->
        val (currentDate, firstPaymentDate) = dates
        val rawPayments = (payments as? Resource.Success)?.data ?: listOf()
        HomeUiState(
            monthName = getMonthName(currentDate.month)?.plus(" - ${currentDate.year}") ?: "",
            //TODO: this should be inside a separated function
            canGoBack = firstPaymentDate != null && moveMonth(by = -1, currentDate = currentDate).let { it  > firstPaymentDate || (it.month == firstPaymentDate.month && it.year == firstPaymentDate.year) },
            canGoNext = currentDate < moveMonth(by = 1, initialDate) && rawPayments.isNotEmpty(),
            payments = rawPayments.mapNotNull { payment -> paymentUiMapper.mapFrom(payment) }.sortedBy { paymentUi -> paymentUi.status },
            errorMessage = (payments as? Resource.Error)?.message ?: state.errorMessage,
            loading = (payments is Resource.Loading),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onEvent(event: HomeEvent) = viewModelScope.launch{
        when(event) {
            HomeEvent.OnBackToPreviousMonth -> {
                _currentDate.update {
                    moveMonth(by = -1, currentDate = it)
                }
            }

            HomeEvent.OnGoToCurrentMonth -> {
                _currentDate.update {
                    initialDate
                }
            }

            HomeEvent.OnGoToNexMonth -> {
                _currentDate.update {
                    moveMonth(by = 1, currentDate = it)
                }
            }

            is HomeEvent.OnPaymentStatusClicked -> {
                updatePaymentStatus(
                    id = event.id,
                    isPayed = event.status != PAYED
                ).doIfSuccess { payment ->
                    eventSyncQueue.enqueue(payment.toSyncEvent("update"))
                }.doIfError { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventJob.cancel()
    }

}