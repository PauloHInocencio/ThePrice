package br.com.noartcode.theprice.ui.presentation.bill.add

import br.com.noartcode.theprice.domain.model.DayMonthAndYear

sealed class AddBillEvent {
    data class OnPriceChanged(val value:String) : AddBillEvent()
    data class OnNameChanged(val name:String) : AddBillEvent()
    data class OnBillingStartDateChanged(val date: DayMonthAndYear) : AddBillEvent()
    data class OnDescriptionChanged(val description:String) : AddBillEvent()
    data object OnSave : AddBillEvent()
}