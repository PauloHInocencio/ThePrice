package br.com.noartcode.theprice.ui.presentation.newbill.model

data class NewBillUiState(
    val price:String = "",
    val name:String = "",
    val dueDate:Int = 1,
    val description:String? = null,
    val isSaving:Boolean = false,
    val isSaved:Boolean = false,
    val errorMessage:String? = null
)
