package br.com.noartcode.theprice.ui.presentation.home.model

import br.com.noartcode.theprice.domain.model.Payment

data class HomeUiState(
    val payments: List<Payment> = listOf(),
    val dateTitle:String = "",
    val loading:Boolean = false,
    val errorMessage:String? = null
)
