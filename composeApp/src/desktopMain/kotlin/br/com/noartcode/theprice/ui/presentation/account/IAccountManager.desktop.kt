package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.BuildKonfig
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.URI
import java.security.MessageDigest
import java.util.UUID

actual class AccountManager : IAccountManager {

    private val redirectUri = "http://localhost:8888/callback"
    private val scopes = listOf("openid", "email", "profile")

    private val ktorClient = HttpClient(OkHttp.create()) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }


    // Authentication via OpenID Connect
    // https://developers.google.com/identity/openid-connect/openid-connect
    override suspend fun signInWithGoogle(): Resource<Pair<String, String>> {
        return try {
            val rawNonce = UUID.randomUUID().toString()
            val hashedNonce = hashNonce(rawNonce)

            val authorizationUrl = buildAuthorizationUrl(hashedNonce)
            withContext(Dispatchers.IO) {
                Desktop.getDesktop().browse(URI(authorizationUrl))
            }

            val code = KtorAuthServer().listenForCode()
                ?: return Resource.Error("Authorization failed")

            val tokenResponse = exchangeCodeForTokens(code)

            val idToken = tokenResponse.idToken
                ?: return Resource.Error("No ID token returned")

            Resource.Success(Pair(idToken, rawNonce))

        } catch (e: Throwable) {
            Resource.Error("Desktop Google Sign-In failed: ${e.message}", exception = e)
        }
    }

    private fun buildAuthorizationUrl(hashedNonce: String): String {
        val scopeParam = scopes.joinToString("%20")
        return buildString {
            append("https://accounts.google.com/o/oauth2/v2/auth")
            append("?client_id=${BuildKonfig.googleAuthClientId}")
            append("&redirect_uri=$redirectUri")
            append("&response_type=code")
            append("&scope=$scopeParam")
            append("&nonce=$hashedNonce")
            append("&access_type=offline")
            append("&prompt=select_account")
        }
    }

    private fun hashNonce(rawNonce: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = digest.digest(rawNonce.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun exchangeCodeForTokens(code: String): TokenResponse {
        return ktorClient.post("https://oauth2.googleapis.com/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                Parameters.build {
                    append("code", code)
                    append("client_id", BuildKonfig.googleAuthClientId)
                    append("client_secret", BuildKonfig.googleAuthSecret)
                    append("redirect_uri", redirectUri)
                    append("grant_type", "authorization_code")
                }.formUrlEncode()
            )
        }.body()
    }

}

@Serializable
private data class TokenResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("expires_in") val expiresIn: Int? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("scope") val scope: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("id_token") val idToken: String? = null
)