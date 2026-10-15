package br.com.noartcode.theprice.ui.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.usecases.settings.IGetAppCurrency
import br.com.noartcode.theprice.domain.usecases.settings.IGetAppLanguage
import br.com.noartcode.theprice.domain.usecases.settings.ISetAppCurrency
import br.com.noartcode.theprice.domain.usecases.settings.ISetAppLanguage
import br.com.noartcode.theprice.domain.usecases.user.IGetUserAccountInfo
import br.com.noartcode.theprice.domain.usecases.user.ILogoutUser
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import theprice.composeapp.generated.resources.Res
import theprice.composeapp.generated.resources.user_login_succeed

class AccountViewModel(
    private val signInUser: ILoginUser,
    private val getAccountInfo: IGetUserAccountInfo,
    private val logOutUser: ILogoutUser,
    private val getAppLanguage: IGetAppLanguage,
    private val setAppLanguage: ISetAppLanguage,
    private val getAppCurrency: IGetAppCurrency,
    private val setAppCurrency: ISetAppCurrency,
) : ViewModel() {

    private val user:StateFlow<User?> = getAccountInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = combine(_uiState, user, getAppLanguage(), getAppCurrency()) { s, u, language, currency ->
        s.copy(
            user = u ?: s.user,
            selectedLanguage = language,
            selectedCurrency = currency,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AccountUiState()
    )


    fun onEvent(event: AccountEvent) {
        when(event){
            AccountEvent.SignInWithGoogle -> {
                signInUser()
                    .onEach { result ->
                        when(result){
                            is Resource.Success -> {
                                val message = getString(Res.string.user_login_succeed)
                                _uiState.update { it.copy(singInStatus = message, loading = false) }
                            }
                            is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message, loading = false) }
                            is Resource.Loading ->  {
                                _uiState.update { it.copy(loading = true) }
                            }
                       }
                   }.launchIn(viewModelScope)
            }

            AccountEvent.LogOutUser -> {
                logOutUser()
                    .onEach { result ->
                        when(result) {
                            is Resource.Success -> {
                                _uiState.update { AccountUiState(loggedOut = true) }
                            }
                            is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message, loading = false) }
                            is Resource.Loading -> _uiState.update { it.copy(loading = true) }
                        }
                    }.launchIn(viewModelScope)
            }

            AccountEvent.ErrorMessageDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            is AccountEvent.LanguageSelected -> {
                if (event.language == uiState.value.selectedLanguage) return
                viewModelScope.launch {
                    setAppLanguage(event.language)
                    _uiState.update { it.copy(showRestartRequiredMessage = true) }
                }
            }

            is AccountEvent.CurrencySelected -> {
                if (event.currency == uiState.value.selectedCurrency) return
                viewModelScope.launch {
                    setAppCurrency(event.currency)
                    _uiState.update { it.copy(showRestartRequiredMessage = true) }
                }
            }

            AccountEvent.RestartMessageDismissed -> {
                _uiState.update { it.copy(showRestartRequiredMessage = false) }
            }
        }
    }
}