package br.com.noartcode.theprice.data.local.datasource.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.noartcode.theprice.data.remote.dtos.UserDto
import br.com.noartcode.theprice.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class SessionStorageImp(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : SessionStorage {
    override suspend fun saveUser(user: UserDto) {
        dataStore.edit {
            it[USER_EMAIL] = user.email
            it[USER_NAME] = user.name
            it[USER_PICTURE] = user.picture
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

    override suspend fun clean() {
        dataStore.edit {
            it.clear()
        }
    }

    override fun getUser(): Flow<User?> =
        dataStore.data.map {
            val email = it[USER_EMAIL]
            val name = it[USER_NAME]
            val picture = it[USER_PICTURE]
            if (email != null && name != null && picture != null) {
                User(
                    email = email,
                    name = name,
                    picture = picture
                )
            } else {
                null
            }
        }.flowOn(ioDispatcher)


    override fun getAccessToken(): Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN] }.flowOn(ioDispatcher)


    override fun getRefreshToken(): Flow<String?> =
        dataStore.data.map { it[REFRESH_TOKEN] }.flowOn(ioDispatcher)


    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PICTURE = stringPreferencesKey("user_picture")
    }
}