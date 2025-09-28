package br.com.noartcode.theprice.data.local.datasource.auth

import br.com.noartcode.theprice.data.remote.dtos.UserCredentialsDto
import br.com.noartcode.theprice.domain.model.User
import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    suspend fun saveCredentials(credentials: UserCredentialsDto)
    suspend fun saveAccessToken(accessToken:String)
    suspend fun saveRefreshToken(refreshToken:String)
    suspend fun saveDeviceID(deviceID:String)
    suspend fun clean()
    fun getUser() : Flow<User?>
    fun getAccessToken() : Flow<String?>
    fun getRefreshToken() : Flow<String?>
    fun getDeviceID() : Flow<String?>
}