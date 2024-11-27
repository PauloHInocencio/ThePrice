package br.com.noartcode.theprice.ui.presentation.home.model

import br.com.noartcode.theprice.domain.model.DayMonthAndYear

data class HomeUiState(
    val currentDate: DayMonthAndYear,
    val payments: List<PaymentUi> = listOf(),
    val monthName:String = "",
    val loading:Boolean = false,
    val canGoBack:Boolean = false,
    val canGoNext:Boolean = false,
    val errorMessage:String? = null,
)
