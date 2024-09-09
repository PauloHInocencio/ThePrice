package br.com.noartcode.theprice.ui.presentation.home.model

sealed class HomeEvent {
    data class OnPaymentChecked(val payment:PaymentUi) : HomeEvent()
    data object OnBackToPreviousMonth : HomeEvent()
    data object OnGoToNexMonth: HomeEvent()
    data object OnGoToCurrentMonth: HomeEvent()
}