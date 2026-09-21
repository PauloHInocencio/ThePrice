package br.com.noartcode.theprice.platform.locale

import br.com.noartcode.theprice.domain.model.AppLanguage

interface ILocaleManager {
    fun apply(language: AppLanguage)
}