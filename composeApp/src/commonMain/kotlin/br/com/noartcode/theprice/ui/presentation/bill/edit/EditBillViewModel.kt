package br.com.noartcode.theprice.ui.presentation.bill.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IDeleteBill
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IUpdateBill
import br.com.noartcode.theprice.ui.presentation.home.views.capitalizeWords
import br.com.noartcode.theprice.ui.presentation.bill.edit.model.EditBillEvent
import br.com.noartcode.theprice.ui.presentation.bill.edit.model.EditBillUiState
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditBillViewModel(
    private val currencyFormatter: ICurrencyFormatter,
    private val updateBill: IUpdateBill,
    private val getTodayDate: IGetTodayDate,
    private val epochFormatter: IEpochMillisecondsFormatter,
    private val getMonthName: IGetMonthName,
    private val getBill: IGetBillByID,
    private val deleteBill: IDeleteBill,
) : ViewModel() {

    private val bill = MutableStateFlow(
        Bill(
            billingStartDate = getTodayDate(),
            createdAt = getTodayDate().toEpochMilliseconds(),
            updatedAt = getTodayDate().toEpochMilliseconds(),
            isSynced = false,
        )
    )
    private val state = MutableStateFlow(EditBillUiState())
    val uiState: StateFlow<EditBillUiState> = combine(state, bill) {
        s, b ->
        val price = currencyFormatter.format(b.price)
        EditBillUiState(
            price = price,
            name = b.name,
            description = b.description,
            isSaving = s.isSaving,
            errorMessage = s.errorMessage,
            billingStartDateTitle = formatTitle(b.billingStartDate),
            billingStartDate = epochFormatter.from(b.billingStartDate),
            priceHasError = price == currencyFormatter.format(0),
            showingConfirmationDialog = s.showingConfirmationDialog,
            canClose = s.canClose,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = EditBillUiState()
        )

    fun onEvent(event: EditBillEvent) = viewModelScope.launch {
        when(event) {
            is EditBillEvent.OnDescriptionChanged -> {
                bill.update { it.copy(description = event.description) }
            }
            is EditBillEvent.OnNameChanged -> {
                bill.update { it.copy(name = event.name) }
            }
            is EditBillEvent.OnPriceChanged -> {
                bill.update { it.copy(price = currencyFormatter.clenup(event.value)) }
            }
            EditBillEvent.OnSave -> {
                state.update { it.copy(isSaving = true) }
                updateBill(bill = bill.value)
                    .doIfSuccess {
                        state.update { it.copy(canClose = true) }
                    }
                    .doIfError { error->
                        state.update { it.copy(errorMessage = error.message) }
                        println("${error.message}, ${error.exception.toString()}" )
                    }
            }

            is EditBillEvent.OnBillingStartDateChanged -> {
                bill.update {
                    it.copy(
                        billingStartDate = epochFormatter.to(event.date),
                    )
                }
            }

            is EditBillEvent.OnGetBill -> {
               bill.update { getBill(event.id)!! }
            }

            EditBillEvent.OnDeleteBill -> {
               state.update {
                   it.copy(showingConfirmationDialog = true)
               }
            }

            EditBillEvent.OnDeleteBillConfirmed -> {
                state.update {
                    it.copy(
                        showingConfirmationDialog = false,
                        isSaving = true
                    )
                }
                deleteBill(bill.value.id)
                state.update {
                    it.copy(
                        canClose = true
                    )
                }
            }

            EditBillEvent.OnDismissConfirmationDialog -> {
                state.update {
                    it.copy(
                        showingConfirmationDialog = false,
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