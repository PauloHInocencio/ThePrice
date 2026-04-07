package br.com.noartcode.theprice.ui.presentation.bill.add

import br.com.noartcode.theprice.domain.model.DayMonthAndYear

data class AddBillUiState(
    val price:String = "",
    val name:String = "",
    val billingStartDateTitle:String = "",
    val billingStartDate: DayMonthAndYear = DayMonthAndYear.EMPTY,
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
