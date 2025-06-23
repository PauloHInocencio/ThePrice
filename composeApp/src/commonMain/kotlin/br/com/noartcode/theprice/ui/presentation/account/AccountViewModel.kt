package br.com.noartcode.theprice.ui.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.noartcode.theprice.domain.model.User
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

class AccountViewModel(
    private val signInUser: ILoginUser,
    private val getAccountInfo: IGetUserAccountInfo,
    private val logOutUser: ILogoutUser,
) : ViewModel() {

    private val user:StateFlow<User?> = getAccountInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )


    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = combine(_uiState, user) { s, u ->
        AccountUiState(
            user = u ?: s.user,
            singInStatus = s.singInStatus,
            loading = s.loading,
            errorMessage = s.errorMessage
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
                                _uiState.update { it.copy(singInStatus = "User Login succeed", loading = false) }
                            }
                            is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message, loading = false) }
                            Resource.Loading ->  {
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
                                _uiState.update { AccountUiState() }
                            }
                            is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message, loading = false) }
                            Resource.Loading -> _uiState.update { it.copy(loading = true) }
                        }
                    }.launchIn(viewModelScope)
            }

            AccountEvent.ErrorMessageDismissed -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }
}