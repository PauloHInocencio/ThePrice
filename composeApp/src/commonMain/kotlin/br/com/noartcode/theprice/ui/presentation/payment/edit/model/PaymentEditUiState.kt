package br.com.noartcode.theprice.ui.presentation.payment.edit.model


data class PaymentEditUiState(
    val billName:String = "",
    val billOriginalPrice:String = "",
    val billDueDate:Long = 0L,
    val billDueDateTitle:String = "",
    val payedValue:String? = null,
    val paidDateTitle:String? = null,
    val paidAtDate:Long? = null,
    val isSaving:Boolean = false,
    val isSaved:Boolean = false,
    val priceHasError:Boolean = false,
) {
    val canSave: Boolean
        get() = !priceHasError
}