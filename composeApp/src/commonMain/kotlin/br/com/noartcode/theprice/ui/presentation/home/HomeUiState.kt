package br.com.noartcode.theprice.ui.presentation.home


data class HomeUiState(
    val paymentSection: List<PaymentSectionUi> = listOf(),
    val monthName:String = "",
    val loading:Boolean = false,
    val canGoBack:Boolean = false,
    val canGoNext:Boolean = false,
    val errorMessage:String? = null,
    val loggedOut: Boolean = false,
)
