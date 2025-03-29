package br.com.noartcode.theprice.ui.presentation.account

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import br.com.noartcode.theprice.ThePriceApp
import br.com.noartcode.theprice.util.Resource
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID

actual class AccountManager(
    private val context: Context
) : IAccountManager {
    override suspend fun signInWithGoogle(): Resource<Pair<String, String>> {
       return try {
            val activity = (context as ThePriceApp).getCurrentActivity() ?: return Resource.Error(
                message = "The application has failed to start the authorization process."
            )
            val credentialManager = CredentialManager.create(activity)


            val rawNonce = UUID.randomUUID().toString()
            val hashedNonce = MessageDigest
                .getInstance("SHA-256")
                .digest(rawNonce.toByteArray())
                .fold("") {str, it -> str + "%02x".format(it)}


            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setNonce(hashedNonce)
                .setServerClientId("350302388547-tke4o9817b8qp74a75nav2te4evb77hd.apps.googleusercontent.com")
                .build()


            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                activity,
                request
            )

            handleSignIn(result, rawNonce)

        } catch (e:Throwable) {
            Resource.Error(message = e.message ?: "Unexpected Error : $e", exception = e)
        }
    }


    private fun handleSignIn(result: GetCredentialResponse, rawNonce:String) : Resource<Pair<String, String>> {

        return when(val credential = result.credential) {
            is PublicKeyCredential -> Resource.Error("Passkey Credential not implemented")
            is PasswordCredential -> Resource.Error("Password Credential not implemented")

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {

                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        Resource.Success(Pair(googleIdTokenCredential.idToken, rawNonce))

                    } catch (e: GoogleIdTokenParsingException) {
                        Resource.Error(
                            message = "Received an invalid google id token response",
                            exception = e
                        )
                    }
                } else {
                    Resource.Error("Credential is not a GoogleIdTokenCredential")
                }
            }
            else -> Resource.Error("Unexpected type of credential")
        }
    }


    @SuppressLint("NewApi")
    private fun computeDigestNonce(json:String) : String {
        // Compute SHA256
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(json.toByteArray())
        return Base64.getUrlEncoder().encodeToString(hashBytes)

    }
}


