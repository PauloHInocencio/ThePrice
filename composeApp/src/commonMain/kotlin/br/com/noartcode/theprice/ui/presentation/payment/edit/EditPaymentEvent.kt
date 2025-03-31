package br.com.noartcode.theprice.ui.presentation.payment.edit


sealed interface EditPaymentEvent {
    data class OnGetPayment(val id:String) : EditPaymentEvent
    data class OnPriceChanged(val value:String) : EditPaymentEvent
    data class OnPaidAtDateChanged(val date:Long) : EditPaymentEvent
    data object OnStatusChanged : EditPaymentEvent
    data object OnChangeAllFuturePayments : EditPaymentEvent
    data object OnChangeOnlyCurrentPayment: EditPaymentEvent
    data object OnDismissConfirmationDialog: EditPaymentEvent
    data object OnSave : EditPaymentEvent
}