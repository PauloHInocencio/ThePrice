package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.util.Resource

interface AuthRepository {
    suspend fun singUpUser(tokenID:String, rawNonce:String) : Resource<Unit>
    suspend fun getUserInfo() : Resource<User>
}