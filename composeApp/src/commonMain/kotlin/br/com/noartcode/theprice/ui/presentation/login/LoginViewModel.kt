package br.com.noartcode.theprice.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.usecases.user.IGetUserAccountInfo
import br.com.noartcode.theprice.domain.usecases.user.ILoginUser
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val loginInUser: ILoginUser,
    private val getAccountInfo: IGetUserAccountInfo,
    private val overdueReminderWorker: IOverduePaymentReminderWorker,
) : ViewModel() {

    private val userInfoState: StateFlow<User?> = getAccountInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = null,
        )

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = combine(_uiState, userInfoState) { ui, user ->
        if (user != null) LoginUiState(isAuthenticated = true)
        else ui
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(500),
        initialValue = LoginUiState()
    )

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.LoginInWithGoogle -> {
                loginInUser()
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> _uiState.update {
                                overdueReminderWorker.sweepDailyDue() //<-- TODO("Move this to an App startup initializer")
                                LoginUiState(isAuthenticated = true)
                            }

                            is Resource.Error -> _uiState.update {
                                LoginUiState(errorMessage = result.message)
                            }

                            is Resource.Loading -> _uiState.update {
                                LoginUiState(isAuthenticatingWithGoogle = true)
                            }
                        }
                    }.launchIn(viewModelScope)
            }

            LoginEvent.ErrorMessageDismissed -> {
                _uiState.update {
                    it.copy(errorMessage = null)
                }
            }

            is LoginEvent.OnEmailChanged -> {
                _uiState.update {
                    it.copy(email = event.value)
                }
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.update {
                    it.copy(password = event.value)
                }
            }

            LoginEvent.LoginInWithEmailAndPassword -> {
                _uiState.update {
                    it.copy(isAuthenticatingWithPassword = true)
                }
            }
        }
    }
}