package br.com.noartcode.theprice.ui.presentation.auth.account.model

import br.com.noartcode.theprice.domain.model.User

data class AccountUiState(
    val user:User? = null,
    val singInStatus:String = "Not Authenticated",
)
