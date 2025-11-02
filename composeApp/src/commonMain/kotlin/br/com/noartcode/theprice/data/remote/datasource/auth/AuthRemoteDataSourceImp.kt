package br.com.noartcode.theprice.data.remote.datasource.auth

import br.com.noartcode.theprice.data.remote.dtos.UserCredentialsDto
import br.com.noartcode.theprice.data.remote.networking.cleanBearerTokens
import br.com.noartcode.theprice.data.remote.networking.safeCall
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class AuthRemoteDataSourceImp(
    private val client: HttpClient,
) : AuthRemoteDataSource {

    override suspend fun signUpUser(tokenID: String, deviceID:String, rawNonce: String): Resource<UserCredentialsDto> =
        safeCall {
            client.post {
                url("users/login")
                setBody(
                    mapOf(
                        "token_id" to tokenID,
                        "raw_nonce" to rawNonce,
                        "device_id" to deviceID,
                    )
                )
            }
        }



    override suspend fun logoutUser(refreshToken:String, deviceID: String): Resource<Unit> =
        safeCall {
            client.post {
                url("users/logout")
                setBody(
                    mapOf("refresh_token" to refreshToken, "device_id" to deviceID)
                )
            }
        }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String,
        deviceID: String,
    ): Resource<UserCredentialsDto> =
        safeCall {
            client.post {
                url("users/create")
                setBody(mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password,
                    "device_id" to deviceID
                ))
            }
        }

    override fun clean() {
        client.cleanBearerTokens()
    }
}