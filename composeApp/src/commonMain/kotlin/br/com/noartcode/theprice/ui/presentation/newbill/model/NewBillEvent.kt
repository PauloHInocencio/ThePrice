package br.com.noartcode.theprice.ui.presentation.newbill.model

sealed class NewBillEvent {
    data class OnPriceChanged(val value:String) : NewBillEvent()
    data class OnNameChanged(val value:String) : NewBillEvent()
    data class OnDueDateChanged(val value:Int) : NewBillEvent()
    data class OnDescriptionChanged(val value:Int) : NewBillEvent()
    data object OnSaveClicked : NewBillEvent()
}