package br.com.noartcode.theprice.platform.locale

import br.com.noartcode.theprice.domain.model.AppLanguage
import java.util.Locale

class DesktopLocaleManager : ILocaleManager {
    override fun apply(language: AppLanguage) {
        Locale.setDefault(Locale.forLanguageTag(language.code))
    }
}
