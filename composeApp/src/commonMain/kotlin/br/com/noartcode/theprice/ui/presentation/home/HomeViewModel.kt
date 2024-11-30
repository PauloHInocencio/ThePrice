package br.com.noartcode.theprice.ui.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.model.HomeEvent
import br.com.noartcode.theprice.ui.presentation.home.model.HomeUiState
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi.Status.PAYED
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val moveMonth: IMoveMonth,
    getFirstPaymentDate: IGetOldestPaymentRecordDate,
    private val updatePayment: IUpdatePayment,
): ViewModel() {
    private val initialDate by lazy { getTodayDay() }

    private val _currentDate = MutableStateFlow(initialDate)
    
    private val _state = MutableStateFlow(HomeUiState())
    val uiState = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            combine(_currentDate, getFirstPaymentDate()) { currentDate, oldestDate -> currentDate to oldestDate }
                .flatMapLatest { (currentDate, oldestDate) ->
                    getPayments(
                        date = currentDate,
                        billStatus = Bill.Status.ACTIVE
                    ).onStart {
                            _state.update { state ->
                                state.copy(
                                    monthName = getMonthName(currentDate.month)?.plus(" - ${currentDate.year}") ?: "",
                                    canGoBack = oldestDate != null && moveMonth(by = -1, currentDate = currentDate) > oldestDate,
                                    canGoNext = currentDate < initialDate,
                                    loading = true,
                                )
                            }
                        }
            }.collect(::handlePaymentsResult)
        }
    }

    fun onEvent(event:HomeEvent) = viewModelScope.launch{
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
                updatePayment(
                    id = event.id,
                    paidAt = if (event.status != PAYED) _currentDate.value else null,
                    payedValue = if (event.status != PAYED)  event.price else null,
                ).doIfError { error ->
                    _state.update { it.copy(errorMessage = error.message) }
                }
            }
        }
    }


    private suspend fun handlePaymentsResult(result: Resource<List<Payment>>) {
        result.doIfSuccess { payments ->
            _state.update {
                it.copy(
                    payments = payments
                        .mapNotNull { payment -> paymentUiMapper.mapFrom(payment) }
                        .sortedBy { paymentUi -> paymentUi.status },
                    loading = false,
                )
            }
        }
            .doIfError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.message,
                        payments = listOf(),
                        loading = false
                    )
                }
            }
    }
}