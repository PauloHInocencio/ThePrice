package br.com.noartcode.theprice.ui.presentation.newbill

import androidx.lifecycle.ViewModel
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillEvent
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NewBillViewModel : ViewModel() {

    private val state = MutableStateFlow(NewBillUiState())
    val uiState = state.asStateFlow()

    fun onEvent(event: NewBillEvent) {
        when(event) {
            is NewBillEvent.OnDescriptionChanged -> {

            }
            is NewBillEvent.OnDueDateChanged -> {

            }
            is NewBillEvent.OnNameChanged -> {

            }
            is NewBillEvent.OnPriceChanged -> {

            }
            NewBillEvent.OnSaveClicked -> {

            }
        }
    }
}