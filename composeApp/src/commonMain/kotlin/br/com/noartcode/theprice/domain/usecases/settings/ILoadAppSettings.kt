package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.data.local.datasource.settings.AppSettingsCache
import br.com.noartcode.theprice.domain.model.AppSettings
import br.com.noartcode.theprice.platform.locale.ILocaleManager
import kotlinx.coroutines.flow.first

fun interface ILoadAppSettings : suspend () -> Unit

class LoadAppSettings(
    private val getAppLanguage: IGetAppLanguage,
    private val getAppCurrency: IGetAppCurrency,
    private val localeManager: ILocaleManager,
    private val cache: AppSettingsCache,
) : ILoadAppSettings {
    override suspend fun invoke() {
        val settings = AppSettings(
            language = getAppLanguage().first(),
            currency = getAppCurrency().first(),
        )
        localeManager.apply(settings.language)
        cache.settings = settings
    }
}
