package br.com.noartcode.theprice.ui.presentation.user.account.model

sealed interface AccountEvent {
    data object SignInWithGoogle : AccountEvent
}