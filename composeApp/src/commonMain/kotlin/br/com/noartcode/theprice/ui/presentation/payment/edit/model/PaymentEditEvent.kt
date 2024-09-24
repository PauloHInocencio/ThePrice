package br.com.noartcode.theprice.ui.presentation.payment.edit.model

sealed class PaymentEditEvent {
    data class OnGetPayment(val id:Long) : PaymentEditEvent()
    data class OnPriceChanged(val value:String) : PaymentEditEvent()
    data class OnPaidAtDateChanged(val date:Long) : PaymentEditEvent()
    data object OnSave : PaymentEditEvent()
}