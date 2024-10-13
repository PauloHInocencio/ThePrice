package br.com.noartcode.theprice.ui.presentation.payment.edit.model

import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi

sealed interface PaymentEditEvent {
    data class OnGetPayment(val id:Long) : PaymentEditEvent
    data class OnPriceChanged(val value:String) : PaymentEditEvent
    data class OnPaidAtDateChanged(val date:Long) : PaymentEditEvent
    data object OnStatusChanged : PaymentEditEvent
    data object OnChangeAllFuturePayments : PaymentEditEvent
    data object OnChangeOnlyCurrentPayment: PaymentEditEvent
    data object OnDismissConfirmationDialog: PaymentEditEvent
    data object OnSave : PaymentEditEvent
}