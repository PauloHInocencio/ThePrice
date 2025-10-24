package br.com.noartcode.theprice.data.local.datasource.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.noartcode.theprice.data.remote.dtos.UserCredentialsDto
import br.com.noartcode.theprice.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AuthLocalDataSourceImp(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher,
) : AuthLocalDataSource {
    override suspend fun saveCredentials(credentials: UserCredentialsDto) {
        dataStore.edit {
            it[USER_EMAIL] = credentials.email
            it[USER_NAME] = credentials.name
            it[USER_PICTURE] = credentials.picture
            it[ACCESS_TOKEN] = credentials.accessToken
            it[REFRESH_TOKEN] = credentials.refreshToken
        }
    }

    override suspend fun saveAccessToken(accessToken: String) {
       dataStore.edit {
           it[ACCESS_TOKEN] = accessToken
       }
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit {
            it[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun saveDeviceID(deviceID: String) {
        dataStore.edit{
            it[DEVICE_ID] = deviceID
        }
    }

    override suspend fun clean() {
        dataStore.edit {
            it.clear()
        }
    }

    override fun getUser(): Flow<User?> =
        dataStore.data.map {
            val email = it[USER_EMAIL] ?: return@map null
            val name = it[USER_NAME] ?: return@map null
            val picture = it[USER_PICTURE]?: ""
            val access = it[ACCESS_TOKEN] ?: return@map null
            val refresh = it[REFRESH_TOKEN] ?: return@map null
            User(
                email = email,
                name = name,
                picture = picture,
                accessToken = access,
                refreshToken = refresh,
            )
        }.flowOn(ioDispatcher)


    override fun getAccessToken(): Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN] }.flowOn(ioDispatcher)


    override fun getRefreshToken(): Flow<String?> =
        dataStore.data.map { it[REFRESH_TOKEN] }.flowOn(ioDispatcher)

    override fun getDeviceID(): Flow<String?> =
        dataStore.data.map { it[DEVICE_ID] }.flowOn(ioDispatcher)



    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PICTURE = stringPreferencesKey("user_picture")
        private val DEVICE_ID = stringPreferencesKey("device_id")
    }
}