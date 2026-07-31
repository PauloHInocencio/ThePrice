package br.com.noartcode.theprice.platform.locale

import br.com.noartcode.theprice.domain.model.AppLanguage
import platform.Foundation.NSUserDefaults

class IOSLocaleManager : ILocaleManager {
    override fun apply(language: AppLanguage) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(language.code), forKey = "AppleLanguages")
    }
}