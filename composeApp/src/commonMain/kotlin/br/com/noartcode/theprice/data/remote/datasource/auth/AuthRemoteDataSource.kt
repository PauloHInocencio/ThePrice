package br.com.noartcode.theprice.data.remote.datasource.auth

import br.com.noartcode.theprice.data.remote.dtos.UserCredentialsDto
import br.com.noartcode.theprice.util.Resource

interface AuthRemoteDataSource {
    suspend fun signUpUser(tokenID:String, deviceID:String, rawNonce:String) : Resource<UserCredentialsDto>
    suspend fun logoutUser(refreshToken:String, deviceID: String) : Resource<Unit>

    suspend fun createUser(name: String, email:String, password: String, deviceID: String) : Resource<UserCredentialsDto>
    fun clean()
}