package br.com.noartcode.theprice.ui.presentation.bill.edit.model

sealed interface EditBillEvent {
    data class OnPriceChanged(val value:String) : EditBillEvent
    data class OnNameChanged(val name:String) : EditBillEvent
    data class OnBillingStartDateChanged(val date:Long) : EditBillEvent
    data class OnDescriptionChanged(val description:String) : EditBillEvent
    data class OnGetBill(val id:Long) : EditBillEvent
    data object OnDeleteBill : EditBillEvent
    data object OnDeleteBillConfirmed : EditBillEvent
    data object OnDismissConfirmationDialog : EditBillEvent
    data object OnSave : EditBillEvent
}