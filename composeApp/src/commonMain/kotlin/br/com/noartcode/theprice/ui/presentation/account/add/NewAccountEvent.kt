package br.com.noartcode.theprice.ui.presentation.account.add

sealed interface NewAccountEvent {
    data class OnNameChanged(val value: String) : NewAccountEvent
    data class OnEmailChanged(val value:String) : NewAccountEvent
    data class OnPasswordChanged(val value:String) : NewAccountEvent
    data object OnCreateNewAccount: NewAccountEvent
    data object ErrorMessageDismissed: NewAccountEvent
}
