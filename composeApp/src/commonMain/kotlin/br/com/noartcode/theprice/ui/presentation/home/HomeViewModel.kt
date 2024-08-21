package br.com.noartcode.theprice.ui.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.ui.presentation.home.model.HomeUiState
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
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
    private val getDateMonthAndYear: IGetDateMonthAndYear,
    private val getMonthName: IGetMonthName
): ViewModel() {


    private data class InternalState(
        val date:String,
        val lastUpdatePayment: Payment? = null
    )

    private val isLoading = MutableStateFlow(false)
    private val monthName = MutableStateFlow<String?>(null)
    private val internalState = MutableStateFlow(InternalState(date = getTodayDay()))
    private val paymentsResultFlow = internalState
        .flatMapLatest {internalState ->
            val (month, year) = getDateMonthAndYear(internalState.date)
            monthName.update { getMonthName(month) }
            getPayments(month, year, billStatus = Bill.Status.ACTIVE)
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
        monthName
    ) { result, isLoading, internal, monthName ->
        HomeUiState(
            payments = if (result is Resource.Success) result.data else listOf(),
            monthName = monthName ?: "",
            loading = isLoading,
            errorMessage = if (result is Resource.Error) result.message else null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}