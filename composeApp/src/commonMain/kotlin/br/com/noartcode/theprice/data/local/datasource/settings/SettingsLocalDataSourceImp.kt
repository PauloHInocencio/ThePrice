package br.com.noartcode.theprice.data.local.datasource.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.noartcode.theprice.domain.model.AppCurrency
import br.com.noartcode.theprice.domain.model.AppLanguage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SettingsLocalDataSourceImp(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher,
) : SettingsLocalDataSource {

    override suspend fun saveLanguage(code: String) {
        dataStore.edit { it[APP_LANGUAGE] = code }
    }

    override fun getLanguage(): Flow<String> =
        dataStore.data
            .map { it[APP_LANGUAGE] ?: AppLanguage.ENGLISH.code } // Default to English
            .flowOn(ioDispatcher)


    override suspend fun saveCurrency(code: String) {
        dataStore.edit { it[APP_CURRENCY] = code }
    }

    override fun getCurrency(): Flow<String> =
        dataStore.data
            .map { it[APP_CURRENCY] ?: AppCurrency.USD.code } // Default to US Dollar
            .flowOn(ioDispatcher)


    companion object {
        private val APP_LANGUAGE = stringPreferencesKey("app_language")
        private val APP_CURRENCY = stringPreferencesKey("app_currency")
    }
}