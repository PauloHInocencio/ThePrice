package br.com.noartcode.theprice.data.remote.networking

import br.com.noartcode.theprice.BuildKonfig
import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.dtos.AccessTokenRequest
import br.com.noartcode.theprice.data.remote.dtos.AccessTokenResponse
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

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

                    val deviceID = localDataSource
                        .getDeviceID()
                        .first() ?: return@refreshTokens null

                    val result = safeCall<AccessTokenResponse> {
                        client.post {
                            markAsRefreshTokenRequest()
                            url("users/renew-access-token")
                            setBody(AccessTokenRequest(
                                refreshToken = refreshToken,
                                deviceID = deviceID,
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
                        is Resource.Error -> {
                            println("Error while authenticating: ${result.message}")
                            null
                        }
                        Resource.Loading -> {
                            println("Invalid response")
                            null
                        }
                    }
                }
            }
        }

        install(SSE){
            maxReconnectionAttempts = 4
            reconnectionTime = 2.seconds
            showCommentEvents()
            showRetryEvents()
        }

        install(HttpTimeout){
            socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            //connectTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        }
        
        defaultRequest {
            url(BuildKonfig.apiBaseUrl)
            contentType(ContentType.Application.Json)
            header("x-api-key", BuildKonfig.apiKey) //TODO("Configure API Key on Webserver")
        }
    }
}