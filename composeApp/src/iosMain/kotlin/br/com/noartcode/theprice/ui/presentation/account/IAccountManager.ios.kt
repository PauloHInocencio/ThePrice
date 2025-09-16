package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.BuildKonfig
import br.com.noartcode.theprice.util.Resource

interface GoogleSignInProvider {
    suspend fun signIn(clientID:String, redirectURI: String) : Pair<String, String>
    fun resume(url: String) : Boolean
}


actual class AccountManager(
    private val signInProvider: GoogleSignInProvider
) : IAccountManager {

    override suspend fun signInWithGoogle(): Resource<Pair<String, String>> {
        return try {
            val redirectURI = BuildKonfig.iosRedirectURI
            val clientID = BuildKonfig.googleAuthClientId
            val result = signInProvider.signIn(clientID = clientID, redirectURI = redirectURI)
            return Resource.Success(result)
        } catch (e: Throwable) {
            Resource.Error("iOS Google Sign-In failed: ${e.message})", exception = e)
        }
    }
}