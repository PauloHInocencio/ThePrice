package br.com.noartcode.theprice.data.remote.datasource.auth

import br.com.noartcode.theprice.data.remote.dtos.AuthInfo
import br.com.noartcode.theprice.util.Resource

interface AuthRemoteDataSource {
    suspend fun signUpUser(tokenID:String, rawNonce:String) : Resource<AuthInfo>
    suspend fun logoutUser() : Resource<Unit>
}