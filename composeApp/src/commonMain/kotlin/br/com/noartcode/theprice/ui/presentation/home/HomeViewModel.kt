package br.com.noartcode.theprice.ui.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.ui.presentation.home.model.HomeUiState
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getDateMonthAndYear: IGetDateMonthAndYear,
    private val getMonthName: IGetMonthName
): ViewModel() {

    private val state = MutableStateFlow(HomeUiState())
    val uiState = state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = HomeUiState()
        )

    init {
        viewModelScope.launch {
            val (month, year) = getDateMonthAndYear(getTodayDay())
            state.update { it.copy(monthName = getMonthName(month) ?: "") }
            getPayments(month = month, year = year, billStatus = Bill.Status.ACTIVE)
            .onEach { state.update { it.copy(loading = true) } }
            .collect{ result ->
                state.update { it.copy(loading = false) }
                when(result){
                    is Resource.Success -> {
                        state.update { it.copy(payments = result.data) }
                    }
                    is Resource.Error -> {
                        state.update { it.copy(errorMessage = result.message) }
                    }
                }
            }
        }
    }
}