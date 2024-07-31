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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HomeViewModel(
    private val getPayments: IGetPayments,
    private val getTodayDay: IGetTodayDate,
    private val getDateMonthAndYear: IGetDateMonthAndYear,
    private val getMonthName: IGetMonthName
): ViewModel() {

    private val state = MutableStateFlow(HomeUiState())
    val uiState = state.asStateFlow()

    init {
        viewModelScope.launch {
            val (month, year) = getDateMonthAndYear(getTodayDay())
            getPayments(
                month = month,
                year = year,
                billStatus = Bill.Status.ACTIVE
            )
        }
    }
}