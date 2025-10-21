package br.com.noartcode.theprice.ui.presentation.account.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NewAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NewAccountUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = NewAccountUiState()
        )

    fun onEvent(event: NewAccountEvent) {
        when (event) {
            else -> TODO()
        }
    }
}