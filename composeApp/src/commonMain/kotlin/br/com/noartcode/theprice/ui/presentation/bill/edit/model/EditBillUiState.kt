package br.com.noartcode.theprice.ui.presentation.bill.edit.model

data class EditBillUiState(
    val price:String = "",
    val name:String = "",
    val billingStartDateTitle:String = "",
    val billingStartDate:Long = 0L,
    val description:String? = null,
    val isSaving:Boolean = false,
    val canClose:Boolean = false,
    val errorMessage:String? = null,
    val priceHasError:Boolean = false,
    val showingConfirmationDialog:Boolean = false,
) {
    val canSave:Boolean
        get() = !nameHasError && !priceHasError
    val nameHasError:Boolean
        get() = name.isEmpty()

}
