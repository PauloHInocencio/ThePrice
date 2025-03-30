package br.com.noartcode.theprice.ui.presentation.login

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading: LoginUiState
    data object LoggedIn: LoginUiState
    data class AuthError(val errorMessage:String) : LoginUiState
}