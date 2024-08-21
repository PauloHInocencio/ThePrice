package br.com.noartcode.theprice.ui.presentation.newbill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
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
    private val formatter: ICurrencyFormatter,
    private val insertNewBill: IInsertNewBill
) : ViewModel() {

    private val bill = MutableStateFlow(Bill())
    private val state = MutableStateFlow(NewBillUiState())
    val uiState: StateFlow<NewBillUiState> = combine(state, bill) {
        s, b ->
        NewBillUiState(
            price = formatter.format(b.price),
            name = b.name,
            dueDate = b.invoiceDueDay,
            description = b.description,
            isSaved = b.id != -1L,
            isSaving = s.isSaving,
            errorMessage = s.errorMessage
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
            is NewBillEvent.OnDueDateChanged -> {
                bill.update { it.copy(invoiceDueDay = event.dueDate) }
            }
            is NewBillEvent.OnNameChanged -> {
                bill.update { it.copy(name = event.name) }
            }
            is NewBillEvent.OnPriceChanged -> {
                bill.update { it.copy(price = formatter.clenup(event.value)) }
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
        }
    }
}