package br.com.noartcode.theprice.ui.presentation.newbill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDaysInMonth
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.ui.presentation.home.views.capitalizeWords
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillEvent
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillUiState
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewBillViewModel(
    private val currencyFormatter: ICurrencyFormatter,
    private val insertNewBill: IInsertNewBill,
    private val getTodayDate: IGetTodayDate,
    private val epochFormatter: IEpochMillisecondsFormatter,
    private val getMonthName: IGetMonthName,
) : ViewModel() {

    private val bill = MutableStateFlow(Bill(createAt = getTodayDate()))
    private val state = MutableStateFlow(NewBillUiState())
    val uiState: StateFlow<NewBillUiState> = combine(state, bill) {
        s, b ->
        val price = currencyFormatter.format(b.price)
        NewBillUiState(
            price = price,
            name = b.name,
            description = b.description,
            isSaved = b.id != -1L,
            isSaving = s.isSaving,
            errorMessage = s.errorMessage,
            selectedDateTitle = formatTitle(b.createAt),
            selectedDate = epochFormatter.from(b.createAt),
            priceHasError = price == currencyFormatter.format(0)
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = NewBillUiState()
        )

    fun onEvent(event: NewBillEvent) {
        when(event) {
            is NewBillEvent.OnDescriptionChanged -> {
                bill.update { it.copy(description = event.description) }
            }
            is NewBillEvent.OnNameChanged -> {
                bill.update { it.copy(name = event.name) }
            }
            is NewBillEvent.OnPriceChanged -> {
                bill.update { it.copy(price = currencyFormatter.clenup(event.value)) }
            }
            NewBillEvent.OnSave -> {
                viewModelScope.launch {
                    state.update { it.copy(isSaving = true) }
                    insertNewBill(bill = bill.value)
                        .doIfSuccess { id ->
                            bill.update { it.copy(id = id) }
                        }
                        .doIfError { error->
                            state.update { it.copy(errorMessage = error.message) }
                            println("${error.message}, ${error.exception.toString()}" )
                        }
                    state.update { it.copy(isSaving = false) }
                }
            }

            is NewBillEvent.OnCratedAtDateChanged -> {
                bill.update {
                    it.copy(
                        createAt = epochFormatter.to(event.date),
                    )
                }
            }
        }
    }

    private fun formatTitle(date:DayMonthAndYear) : String {
        val monthName =
            getMonthName(date.month)
                ?.take(3)
                ?.capitalizeWords()
                ?.plus(" ${date.year}") ?: return ""
        val day = if (date.day < 10) "0${date.day}" else date.day.toString()
        return "$day $monthName"
    }
}