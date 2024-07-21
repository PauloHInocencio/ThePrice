package br.com.noartcode.theprice.ui.presentation.newbill.model

data class NewBillUiState(
    val price:String? = null,
    val name:String = "",
    val dueDate:Int = 1,
    val description:String? = null,
)
