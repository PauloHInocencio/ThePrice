package br.com.noartcode.theprice.ui.presentation.user.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.ui.presentation.user.account.model.AccountEvent
import br.com.noartcode.theprice.ui.presentation.user.account.model.AccountUiState
import br.com.noartcode.theprice.util.doIfError
import br.com.noartcode.theprice.util.doIfSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val accountManager: IAccountManager,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountUiState())
    val uiState = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AccountUiState()
    )


    fun onEvent(event: AccountEvent) = viewModelScope.launch{
        when(event){
            AccountEvent.SignInWithGoogle -> {
                accountManager.singInWithGoogle()
                    .doIfSuccess { token ->
                        _state.update { it.copy(singInStatus = "Authenticated - token: $token") }
                    }
                    .doIfError { error ->
                        _state.update { it.copy(singInStatus = "Error while authenticating: ${error.message}") }
                    }
            }
        }
    }
}