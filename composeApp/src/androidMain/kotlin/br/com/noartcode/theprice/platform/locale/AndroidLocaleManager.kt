package br.com.noartcode.theprice.platform.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import br.com.noartcode.theprice.domain.model.AppLanguage

class AndroidLocaleManager : ILocaleManager {
    override fun apply(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
    }
}