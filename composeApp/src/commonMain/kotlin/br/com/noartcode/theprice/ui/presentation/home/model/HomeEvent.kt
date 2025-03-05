package br.com.noartcode.theprice.ui.presentation.home.model

sealed class HomeEvent {
    data class OnPaymentStatusClicked(val id:String, val status:PaymentUi.Status) : HomeEvent()
    data object OnBackToPreviousMonth : HomeEvent()
    data object OnGoToNexMonth: HomeEvent()
    data object OnGoToCurrentMonth: HomeEvent()
}