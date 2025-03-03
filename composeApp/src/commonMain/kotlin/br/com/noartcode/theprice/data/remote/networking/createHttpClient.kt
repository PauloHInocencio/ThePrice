package br.com.noartcode.theprice.data.remote.networking

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.dtos.AccessTokenRequest
import br.com.noartcode.theprice.data.remote.dtos.AccessTokenResponse
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

fun createHttpClient(engine: HttpClientEngine, localDataSource: SessionStorage) : HttpClient {
    return HttpClient(engine) {
        install(ContentNegotiation){
            json(
                json = Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            level = LogLevel.ALL
        }
        install(Auth) {
            bearer {
                refreshTokens {
                    val refreshToken = localDataSource
                        .getRefreshToken()
                        .first() ?: return@refreshTokens null


                    val result = safeCall<AccessTokenResponse> {
                        client.post {
                            markAsRefreshTokenRequest()
                            url("users/renew-access-token")
                            setBody(AccessTokenRequest(
                                refreshToken = refreshToken
                            ))
                        }
                    }


                    when(result){
                        is Resource.Success -> {
                            localDataSource.saveAccessToken(result.data.accessToken)
                            BearerTokens(
                                accessToken = result.data.accessToken,
                                refreshToken = localDataSource.getAccessToken().first()
                            )
                        }
                        else -> null
                    }
                }
            }
        }
        defaultRequest {
            url(API_BASE_URL)
            contentType(ContentType.Application.Json)
            //header("x-api-key", "") //TODO("Configure API Key on Webserver")
        }
    }
}