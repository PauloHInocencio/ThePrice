package br.com.noartcode.theprice.ui.presentation.newbill

import androidx.lifecycle.ViewModel
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillEvent
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NewBillViewModel(
    private val formatter: ICurrencyFormatter
) : ViewModel() {

    private val state = MutableStateFlow(NewBillUiState())
    val uiState = state.asStateFlow()

    fun onEvent(event: NewBillEvent) {
        when(event) {
            is NewBillEvent.OnDescriptionChanged -> {
                state.update { it.copy(description = event.description) }
            }
            is NewBillEvent.OnDueDateChanged -> {
                state.update { it.copy(dueDate = event.dueDate) }
            }
            is NewBillEvent.OnNameChanged -> {
                state.update { it.copy(name = event.name) }
            }
            is NewBillEvent.OnPriceChanged -> {
                state.update { it.copy(price = formatter(event.value)) }
            }
            NewBillEvent.OnSaveClicked -> {

            }
        }
    }
}