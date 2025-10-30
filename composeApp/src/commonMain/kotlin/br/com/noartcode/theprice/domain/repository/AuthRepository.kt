package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.util.Resource

interface AuthRepository {
    suspend fun signUpUser(tokenID:String, rawNonce:String) : Resource<User>
    suspend fun getUserInfo() : Resource<User>
    suspend fun createUser(name:String, email:String, password:String) : Resource<User>
    suspend fun logoutUser() : Resource<Unit>
}