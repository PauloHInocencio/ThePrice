package br.com.noartcode.theprice.ui.presentation.account.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.usecases.user.IRegisterUser
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class NewAccountViewModel(
    private val registerUser: IRegisterUser
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewAccountUiState())
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = NewAccountUiState()
        )

    fun onEvent(event: NewAccountEvent) {
        when (event) {
            NewAccountEvent.ErrorMessageDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            is NewAccountEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.value) }
            }
            is NewAccountEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.value) }
            }
            is NewAccountEvent.OnNameChanged -> {
                _uiState.update { it.copy(name = event.value) }
            }

            NewAccountEvent.OnCreateNewAccount -> {
                registerUser(
                    name = _uiState.value.name,
                    email = uiState.value.email,
                    password = uiState.value.password
                ).onEach { result ->
                    when(result) {
                        is Resource.Error -> {
                            _uiState.update { it.copy(errorMessage = result.message, isCreating = false) }
                        }
                        Resource.Loading -> {
                            _uiState.update { it.copy(isCreating = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update { NewAccountUiState(isCreated = true) }
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }
}