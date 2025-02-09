package br.com.noartcode.theprice.ui.presentation.auth.account.model

sealed interface AccountEvent {
    data object SignInWithGoogle : AccountEvent
    data object LogOutUser: AccountEvent
    data object ErrorMessageDismissed : AccountEvent
}