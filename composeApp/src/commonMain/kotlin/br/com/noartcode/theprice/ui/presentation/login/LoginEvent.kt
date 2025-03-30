package br.com.noartcode.theprice.ui.presentation.login

sealed interface LoginEvent {
    data object LoginInWithGoogle: LoginEvent
    data object ErrorMessageDismissed: LoginEvent
}
