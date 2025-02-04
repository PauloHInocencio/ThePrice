package br.com.noartcode.theprice.data.local.datasource.auth

import br.com.noartcode.theprice.data.remote.dtos.UserDto
import br.com.noartcode.theprice.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    suspend fun saveUser(user: UserDto)
    suspend fun saveAccessToken(accessToken:String)
    suspend fun saveRefreshToken(refreshToken:String)
    fun getUser() : Flow<User?>
    fun getAccessToken() : Flow<String?>
    fun getRefreshToken() : Flow<String?>
}