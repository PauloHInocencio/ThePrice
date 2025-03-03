package br.com.noartcode.theprice.data.remote.datasource.auth

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.dtos.AuthInfo
import br.com.noartcode.theprice.data.remote.networking.safeCall
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.first

class AuthRemoteDataSourceImp(
    private val client: HttpClient,
    private val session: SessionStorage
) : AuthRemoteDataSource {

    override suspend fun signUpUser(tokenID: String, rawNonce: String): Resource<AuthInfo> =
        safeCall {
            client.post {
                url("users/login")
                setBody(mapOf("token_id" to tokenID, "raw_nonce" to rawNonce))
            }
        }



    override suspend fun logoutUser(): Resource<Unit> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            val refreshToken = session.getRefreshToken().first()
            client.post {
                url("users/logout")
                setBody(mapOf("refresh_token" to refreshToken))
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

}