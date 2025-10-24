package br.com.noartcode.theprice.ui.presentation.login

sealed interface LoginEvent {

    data class OnEmailChanged(val value:String) : LoginEvent
    data class OnPasswordChanged(val value:String) : LoginEvent
    data object LoginInWithGoogle: LoginEvent
    data object LoginInWithEmailAndPassword: LoginEvent
    data object ErrorMessageDismissed: LoginEvent
}
