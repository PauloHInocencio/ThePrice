package br.com.noartcode.theprice.ui.presentation.payment.edit


sealed interface PaymentEditEvent {
    data class OnGetPayment(val id:String) : PaymentEditEvent
    data class OnPriceChanged(val value:String) : PaymentEditEvent
    data class OnPaidAtDateChanged(val date:Long) : PaymentEditEvent
    data object OnStatusChanged : PaymentEditEvent
    data object OnChangeAllFuturePayments : PaymentEditEvent
    data object OnChangeOnlyCurrentPayment: PaymentEditEvent
    data object OnDismissConfirmationDialog: PaymentEditEvent
    data object OnSave : PaymentEditEvent
}