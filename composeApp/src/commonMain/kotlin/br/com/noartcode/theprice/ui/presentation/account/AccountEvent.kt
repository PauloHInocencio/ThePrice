package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.domain.model.AppCurrency
import br.com.noartcode.theprice.domain.model.AppLanguage

sealed interface AccountEvent {
    data object LogoutRequested : AccountEvent
    data object LogoutConfirmed : AccountEvent
    data object ForceLogoutConfirmed : AccountEvent
    data object LogoutDialogDismissed : AccountEvent
    data class LanguageSelected(val language: AppLanguage) : AccountEvent
    data class CurrencySelected(val currency: AppCurrency) : AccountEvent
    data object RestartMessageDismissed : AccountEvent
}
