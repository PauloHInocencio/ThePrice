package br.com.noartcode.theprice.ui.presentation.home.model

data class HomeUiState(
    val payments: List<PaymentUi> = listOf(),
    val monthName:String = "",
    val loading:Boolean = false,
    val errorMessage:String? = null
)
