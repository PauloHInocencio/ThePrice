package br.com.noartcode.theprice.ui.presentation.home.model

sealed class HomeEvent {
    data class OnPaymentStatusClicked(val id:Long, val status:PaymentUi.Status, val price:String) : HomeEvent()
    data object OnBackToPreviousMonth : HomeEvent()
    data object OnGoToNexMonth: HomeEvent()
    data object OnGoToCurrentMonth: HomeEvent()
}