package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.domain.model.User

data class AccountUiState(
    val user:User? = null,
    val singInStatus:String = "",
    val loading:Boolean = false,
    val errorMessage:String? = null,
)
