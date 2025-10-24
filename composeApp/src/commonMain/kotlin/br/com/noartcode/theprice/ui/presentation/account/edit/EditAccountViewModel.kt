package br.com.noartcode.theprice.ui.presentation.account.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class EditAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditAccountUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = EditAccountUiState()
        )

    fun onEvent(event: EditAccountEvent) {
        when (event) {
            else -> TODO()
        }
    }
}