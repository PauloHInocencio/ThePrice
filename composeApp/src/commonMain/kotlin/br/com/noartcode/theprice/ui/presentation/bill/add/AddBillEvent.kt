package br.com.noartcode.theprice.ui.presentation.bill.add

sealed class AddBillEvent {
    data class OnPriceChanged(val value:String) : AddBillEvent()
    data class OnNameChanged(val name:String) : AddBillEvent()
    data class OnBillingStartDateChanged(val date:Long) : AddBillEvent()
    data class OnDescriptionChanged(val description:String) : AddBillEvent()
    data object OnSave : AddBillEvent()
}