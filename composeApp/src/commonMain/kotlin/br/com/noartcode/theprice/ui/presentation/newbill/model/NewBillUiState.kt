package br.com.noartcode.theprice.ui.presentation.newbill.model

data class NewBillUiState(
    val price:String = "",
    val name:String = "",
    val selectedDateTitle:String = "",
    val selectedDate:Long = 0L,
    val description:String? = null,
    val isSaving:Boolean = false,
    val isSaved:Boolean = false,
    val errorMessage:String? = null,
    val priceHasError:Boolean = false,
) {
    val canSave:Boolean
        get() = !nameHasError && !priceHasError
    val nameHasError:Boolean
        get() = name.isEmpty()

}
