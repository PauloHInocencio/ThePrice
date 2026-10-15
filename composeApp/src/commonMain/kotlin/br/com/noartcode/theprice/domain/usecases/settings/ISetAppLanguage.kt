package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.data.local.datasource.settings.SettingsLocalDataSource
import br.com.noartcode.theprice.domain.model.AppLanguage
import br.com.noartcode.theprice.platform.locale.ILocaleManager

fun interface ISetAppLanguage : suspend (AppLanguage) -> Unit

class SetAppLanguage(
    private val localDataSource: SettingsLocalDataSource,
    private val localeManager: ILocaleManager,
) : ISetAppLanguage {
    override suspend fun invoke(language: AppLanguage) {
        localDataSource.saveLanguage(language.code)
        localeManager.apply(language)
    }
}
