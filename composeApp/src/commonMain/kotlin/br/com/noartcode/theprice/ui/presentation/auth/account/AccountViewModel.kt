package br.com.noartcode.theprice.ui.presentation.auth.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.usecases.IGetUserInfo
import br.com.noartcode.theprice.domain.usecases.ISignInUser
import br.com.noartcode.theprice.ui.presentation.auth.account.model.AccountEvent
import br.com.noartcode.theprice.ui.presentation.auth.account.model.AccountUiState
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val signInUser: ISignInUser,
    private val getUserInfo: IGetUserInfo,
) : ViewModel() {

    private val user:StateFlow<User?> = getUserInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    private val _state = MutableStateFlow(AccountUiState())
    val uiState = combine(_state, user) { s, u ->
        AccountUiState(
            user = u ?: s.user,
            singInStatus = s.singInStatus
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AccountUiState()
    )


    fun onEvent(event: AccountEvent) = viewModelScope.launch{
        when(event){
            AccountEvent.SignInWithGoogle -> {
                when(val result = signInUser()){
                    is Resource.Success -> {
                        _state.update { it.copy(singInStatus = "User Login succeed") }
                    }
                    is Resource.Error -> _state.update { it.copy(singInStatus = result.message) }
                    Resource.Loading -> Unit
                }
            }
        }
    }
}