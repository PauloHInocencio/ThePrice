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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val moveMonth: IMoveMonth,
    private val getFirstPaymentDate: IGetOldestPaymentRecordDate,
    private val updatePayment: IUpdatePayment,
): ViewModel() {
    private val initialDate by lazy { getTodayDay() }
    private val oldestDate = getFirstPaymentDate()
    private val _state = MutableStateFlow(HomeUiState(initialDate))
    val uiState = combine(
        _state,
        oldestDate,
    ) { state, oldestDate ->
        HomeUiState(
            currentDate = state.currentDate,
            payments = state.payments,
            monthName = getMonthName(state.currentDate.month)?.plus(" - ${state.currentDate.year}") ?: "",
            loading = state.loading,
            errorMessage = state.errorMessage,
            canGoBack = oldestDate != null && moveMonth(by = -1, currentDate = state.currentDate) > oldestDate,
            canGoNext = state.currentDate < initialDate,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(initialDate)
    )

    init {
        viewModelScope.launch {
            getPayments(initialDate)
        }
    }

    fun onEvent(event:HomeEvent) = viewModelScope.launch{
        when(event) {
            HomeEvent.OnBackToPreviousMonth -> {
                getPayments(moveMonth(by = -1, currentDate = _state.value.currentDate))
            }

            HomeEvent.OnGoToCurrentMonth -> {
                getPayments(getTodayDay())
            }

            HomeEvent.OnGoToNexMonth -> {
                getPayments(moveMonth(by = 1, currentDate = _state.value.currentDate))
            }

            is HomeEvent.OnPaymentStatusClicked -> {
                updatePayment(
                    id = event.id,
                    paidAt = if (event.status != PAYED) _state.value.currentDate else null,
                    payedValue = if (event.status != PAYED)  event.price else null,
                )
                    .doIfSuccess {
                        getPayments(_state.value.currentDate)
                    }
                    .doIfError { error ->
                        throw error.exception ?: Exception("Some Error Have Occur")
                    }
            }
        }
    }


    private suspend fun getPayments(date:DayMonthAndYear)  {
        getPayments(date = date, billStatus = Bill.Status.ACTIVE)
            .onStart { _state.update { it.copy(loading = true) } }
            .catch { exception ->
                emit(Resource.Error(message = exception.message ?: "Unknown error"))
            }
            .first()
            .doIfSuccess { payments ->
                _state.update {
                    it.copy(
                        payments = payments.mapNotNull { paymentUiMapper.mapFrom(it) }.sortedBy { it.status },
                        currentDate = date,
                        loading = false
                    )
                }
            }
            .doIfError { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.message,
                        payments = listOf(),
                        currentDate = date,
                        loading = false
                    )
                }
            }
    }
}