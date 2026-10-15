package br.com.noartcode.theprice.ui.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.domain.usecases.user.ILoginUser
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LoginViewModel(
    private val loginInUser: ILoginUser,
    private val overdueReminderWorker: IOverduePaymentReminderWorker,
) : ViewModel() {

    // Only the login flow result drives navigation. Already-logged-in users are routed to
    // Home by SplashViewModel and never reach this screen.
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            LoginEvent.LoginInWithGoogle -> {
                loginInUser()
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                // Bills were just stored: restart the schedule so the first sweep runs now with fresh data
                                overdueReminderWorker.sweepDailyDue(restart = true)
                                _uiState.update { it.copy(isAuthenticated = true) }
                            }

                            is Resource.Error -> _uiState.update {
                                LoginUiState(errorMessage = result.message)
                            }

                            is Resource.Loading -> _uiState.update {
                                LoginUiState(isAuthenticatingWithGoogle = true)
                            }
                        }
                    }
                    .catch { e ->
                        _uiState.update {
                            LoginUiState(errorMessage = e.message ?: "Unexpected error while logging in")
                        }
                    }
                    .launchIn(viewModelScope)
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
