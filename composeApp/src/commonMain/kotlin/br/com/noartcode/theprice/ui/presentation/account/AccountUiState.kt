package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.domain.model.AppCurrency
import br.com.noartcode.theprice.domain.model.AppLanguage
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.ui.views.LogoutDialog

data class AccountUiState(
    val user:User? = null,
    val singInStatus:String = "",
    val loading:Boolean = false,
    val errorMessage:String? = null,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val selectedCurrency: AppCurrency = AppCurrency.USD,
    val showRestartRequiredMessage: Boolean = false,
    val loggedOut: Boolean = false,
    val logoutDialog: LogoutDialog? = null,
)
