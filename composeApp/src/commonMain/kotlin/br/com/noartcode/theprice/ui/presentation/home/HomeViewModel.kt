package br.com.noartcode.theprice.ui.presentation.home


import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val moveMonth: IMoveMonth,
    private val getFirstPaymentDate: IGetOldestPaymentRecordDate,
    private val updatePayment: IUpdatePayment,
): ViewModel() {


    private data class InternalState(
        val currentDay:DayMonthAndYear,
        val lastUpdatePayment: Payment? = null,
    )

    private val oldestDate = getFirstPaymentDate()
    private val isLoading = MutableStateFlow(false)
    private val monthName = MutableStateFlow<String?>(null)
    private val internalState = MutableStateFlow(InternalState(currentDay = getTodayDay()))
    private val paymentsResultFlow = internalState
        .flatMapLatest {internalState ->
            val date = internalState.currentDay
            monthName.update { getMonthName(date.month)?.plus(" - ${date.year}") }
            getPayments(date = date, billStatus = Bill.Status.ACTIVE)
                .onStart { isLoading.update { true } }
                .onEach {
                    isLoading.update { false }
                }
        }.shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 1
        )

    val uiState = combine(
        paymentsResultFlow,
        isLoading,
        internalState,
        monthName,
        oldestDate,
    ) { result, isLoading, internal, monthName, oldestDate ->
        HomeUiState(
            payments = if (result is Resource.Success) {
                result.data.mapNotNull { paymentUiMapper.mapFrom(it) }.sortedBy { it.status }
            } else listOf(),
            monthName = monthName ?: "",
            loading = isLoading,
            errorMessage = if (result is Resource.Error) {
                result.message
            } else null,
            canGoBack = oldestDate != null && moveMonth(by = -1, currentDate = internal.currentDay) > oldestDate,
            canGoNext = internal.currentDay < getTodayDay(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )


    fun onEvent(event:HomeEvent) = viewModelScope.launch{
        when(event) {
            HomeEvent.OnBackToPreviousMonth -> {
                internalState.update {
                    it.copy(currentDay = moveMonth(by = -1, currentDate = it.currentDay) )
                }
            }

            HomeEvent.OnGoToCurrentMonth -> {
                internalState.update {
                    it.copy(currentDay = getTodayDay())
                }
            }

            HomeEvent.OnGoToNexMonth -> {
                internalState.update {
                    it.copy(currentDay =  moveMonth(by = 1, currentDate = it.currentDay) )
                }
            }

            is HomeEvent.OnPaymentStatusClicked -> {
                updatePayment(
                    id = event.id,
                    paidAt = if (event.status != PAYED) internalState.value.currentDay else null,
                    payedValue = if (event.status != PAYED)  event.price else null,
                )
                    .doIfSuccess { data ->
                        internalState.update { it.copy(lastUpdatePayment = data) }
                    }
                    .doIfError { error ->
                        throw error.exception ?: Exception("Some Error Have Occur")
                    }
            }
        }
    }
}