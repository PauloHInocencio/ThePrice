package br.com.noartcode.theprice.data.local.datasource.settings

import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {
    suspend fun saveLanguage(code: String)
    fun getLanguage() : Flow<String>
    suspend fun saveCurrency(code: String)
    fun getCurrency() : Flow<String>
}