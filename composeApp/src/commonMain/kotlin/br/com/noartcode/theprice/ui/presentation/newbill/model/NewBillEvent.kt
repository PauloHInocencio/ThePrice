package br.com.noartcode.theprice.ui.presentation.newbill.model

sealed class NewBillEvent {
    data class OnPriceChanged(val value:Int) : NewBillEvent()
    data class OnNameChanged(val name:String) : NewBillEvent()
    data class OnDueDateChanged(val dueDate:Int) : NewBillEvent()
    data class OnDescriptionChanged(val description:String) : NewBillEvent()
    data object OnSaveClicked : NewBillEvent()
}