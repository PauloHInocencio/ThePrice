package br.com.noartcode.theprice.ui.presentation.payment.edit.model

import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi


data class PaymentEditUiState(
    val billName:String = "",
    val paymentStatus: PaymentUi.Status = PaymentUi.Status.PENDING,
    val payedValue:String = "",
    val paidDateTitle:String = "",
    val paidAtDate:Long = 0L,
    val isSaving:Boolean = false,
    val isSaved:Boolean = false,
    val askingConfirmation:Boolean = false,
    val priceHasError:Boolean = false,
    val priceHasChanged:Boolean = false,
    val dateHasChanged:Boolean = false,
    val statusHasChanged:Boolean = false,
) {
    val canSave: Boolean
        get() = !priceHasError && (dateHasChanged || priceHasChanged || statusHasChanged)
}