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
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.presentation.home.model.HomeEvent
import br.com.noartcode.theprice.ui.presentation.home.model.HomeUiState
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getMonthName: IGetMonthName,
    private val paymentUiMapper: UiMapper<Payment, PaymentUi?>,
    private val moveMonth: IMoveMonth,
    private val getFirstPaymentDate: IGetOldestPaymentRecordDate,
): ViewModel() {


    private data class InternalState(
        val date:DayMonthAndYear,
        val lastUpdatePayment: Payment? = null,
    )

    private val oldestDate = getFirstPaymentDate()
    private val isLoading = MutableStateFlow(false)
    private val monthName = MutableStateFlow<String?>(null)
    private val internalState = MutableStateFlow(InternalState(date = getTodayDay()))
    private val paymentsResultFlow = internalState
        .flatMapLatest {internalState ->
            val date = internalState.date
            monthName.update { getMonthName(date.month)?.plus(" - ${date.year}") }
            getPayments(date = date, billStatus = Bill.Status.ACTIVE)
                .onStart { isLoading.update { true } }
                .onCompletion { isLoading.update { false } }
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
            canGoBack = oldestDate != null && internal.date > oldestDate,
            canGoNext = internal.date < getTodayDay(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )


    fun onEvent(event:HomeEvent) {
        when(event) {
            HomeEvent.OnBackToPreviousMonth -> {
                internalState.update {
                    it.copy(date = moveMonth(by = -1, currentDate = it.date) )
                }
            }

            HomeEvent.OnGoToCurrentMonth -> {
                internalState.update {
                    it.copy(date = getTodayDay())
                }
            }

            HomeEvent.OnGoToNexMonth -> {
                internalState.update {
                    it.copy(date =  moveMonth(by = 1, currentDate = it.date) )
                }
            }

            is HomeEvent.OnPaymentChecked -> {

            }
        }
    }
}