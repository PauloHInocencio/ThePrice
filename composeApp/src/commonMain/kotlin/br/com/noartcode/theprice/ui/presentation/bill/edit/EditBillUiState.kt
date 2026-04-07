package br.com.noartcode.theprice.ui.presentation.bill.edit

import br.com.noartcode.theprice.domain.model.DayMonthAndYear

data class EditBillUiState(
    val price:String = "",
    val name:String = "",
    val billingStartDateTitle:String = "",
    val billingStartDate: DayMonthAndYear = DayMonthAndYear.EMPTY,
    val description:String? = null,
    val isSaving:Boolean = false,
    val canClose:Boolean = false,
    val errorMessage:String? = null,
    val priceHasError:Boolean = true,
    val showingConfirmationDialog:Boolean = false,
    val showingChangePropagationDialog: Boolean = false,
) {
    val canSave:Boolean
        get() = !nameHasError && !priceHasError && errorMessage == null
    val nameHasError:Boolean
        get() = name.isEmpty()

}
