package br.com.noartcode.theprice.ui.presentation.newbill.model

sealed class NewBillEvent {
    data class OnPriceChanged(val value:String) : NewBillEvent()
    data class OnNameChanged(val name:String) : NewBillEvent()
    data class OnCratedAtDateChanged(val date:Long) : NewBillEvent()
    data class OnDescriptionChanged(val description:String) : NewBillEvent()
    data object OnSave : NewBillEvent()
}