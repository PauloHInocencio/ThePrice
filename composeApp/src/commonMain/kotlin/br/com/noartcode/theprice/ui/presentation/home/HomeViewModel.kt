package br.com.noartcode.theprice.ui.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.EventDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.data.remote.mapper.toDomain
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.IDeleteBill
import br.com.noartcode.theprice.domain.usecases.IGetEvents
import br.com.noartcode.theprice.domain.usecases.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertBill
import br.com.noartcode.theprice.domain.usecases.IInsertMissingPayments
import br.com.noartcode.theprice.domain.usecases.IInsertPayments
import br.com.noartcode.theprice.domain.usecases.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.IUpdateBill
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.IUpdatePaymentStatus
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
    //private val deleteBill: IDeleteBill,
    private val insertNewBill: IInsertBill,
    private val updatePayment: IUpdatePayment,
    private val insertMissingPayments: IInsertMissingPayments,
    private val insertPayments: IInsertPayments,
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
                    ("payment" to "create") -> {
                        val payments = defaultJson.decodeFromString<List<PaymentDto>>(event.data!!)
                        insertPayments(payments.toDomain())
                    }
                    ("payment" to  "update") -> {
                        val payment = defaultJson.decodeFromString<PaymentDto>(event.data!!)
                        val paymentDomain = payment.toDomain()
                        updatePayment(paymentDomain)
                            .doIfError { error ->
                                println("error during payment update: ${error.message}")
                            }
                            .doIfSuccess {
                                println("success during payment update.")
                            }
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
                    insertMissingPayments(currentDate)
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
        HomeUiState(
            monthName = getMonthName(currentDate.month)?.plus(" - ${currentDate.year}") ?: "",
            canGoBack = firstPaymentDate != null && moveMonth(by = -1, currentDate = currentDate).let { it  > firstPaymentDate || (it.month == firstPaymentDate.month && it.year == firstPaymentDate.year) },
            canGoNext = currentDate < initialDate,
            payments = (payments as? Resource.Success)?.data?.mapNotNull { payment -> paymentUiMapper.mapFrom(payment) }?.sortedBy { paymentUi -> paymentUi.status } ?: emptyList(),
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
                ).doIfSuccess {

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