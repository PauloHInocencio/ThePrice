package br.com.noartcode.theprice.data.remote.datasource.auth

import br.com.noartcode.theprice.data.remote.dtos.AuthInfo
import br.com.noartcode.theprice.data.remote.networking.post
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient

class AuthRemoteDataSourceImp(
    private val client: HttpClient
) : AuthRemoteDataSource {

    override suspend fun signUpUser(tokenID: String, rawNonce: String): Resource<AuthInfo> =
        client.post(
            route ="users/login",
            body = mapOf("token_id" to tokenID, "raw_nonce" to rawNonce)
        )
}