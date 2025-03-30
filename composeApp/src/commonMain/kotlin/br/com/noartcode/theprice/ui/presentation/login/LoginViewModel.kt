package br.com.noartcode.theprice.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.usecases.ILoginUser
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val loginInUser: ILoginUser,
) : ViewModel() {


    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = LoginUiState.Idle
        )

    fun onEvent(event: LoginEvent) {
        when(event) {
            LoginEvent.LoginInWithGoogle -> {
                loginInUser()
                    .onEach { result ->
                        when(result) {
                            is Resource.Success -> {
                                _uiState.update { LoginUiState.LoggedIn  }
                            }
                            is Resource.Error -> _uiState.update { LoginUiState.AuthError(errorMessage = result.message) }
                            is Resource.Loading -> _uiState.update { LoginUiState.Loading }

                        }
                    }.launchIn(viewModelScope)
            }

            LoginEvent.ErrorMessageDismissed -> {
                _uiState.update { LoginUiState.Idle }
            }
        }
    }
}