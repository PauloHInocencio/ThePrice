package br.com.noartcode.theprice.ui.presentation.bill.edit

import br.com.noartcode.theprice.domain.model.DayMonthAndYear

sealed interface EditBillEvent {
    data class OnPriceChanged(val value:String) : EditBillEvent
    data class OnNameChanged(val name:String) : EditBillEvent
    data class OnBillingStartDateChanged(val date: DayMonthAndYear) : EditBillEvent
    data class OnDescriptionChanged(val description:String) : EditBillEvent
    data object OnDeleteBill : EditBillEvent
    data object OnDeleteBillConfirmed : EditBillEvent
    data object OnDismissConfirmationDialog : EditBillEvent
    data object OnDismissErrorMessage : EditBillEvent
    data object OnSave : EditBillEvent
    data object OnDismissChangePropagationDialog : EditBillEvent
    data object OnApplyChangesToAllPayments : EditBillEvent
    data object OnApplyChangesToFuturePayments : EditBillEvent
}