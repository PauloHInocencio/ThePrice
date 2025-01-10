package br.com.noartcode.theprice.ui.presentation.user.account

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

actual class AccountManager(
    private val context: Context
) : IAccountManager {
    override suspend fun singInWithGoogle(): Resource<String> {
       return try {
            val activity = (context as ThePriceApp).getCurrentActivity() ?: return Resource.Error(
                message = "The application has failed to start the authorization process."
            )
            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("350302388547-tke4o9817b8qp74a75nav2te4evb77hd.apps.googleusercontent.com")
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                activity,
                request
            )

           handleSignIn(result)

        } catch (e:Throwable) {
            Resource.Error(message = e.message ?: "Unexpected Error : $e", exception = e)
        }
    }


    private fun handleSignIn(result: GetCredentialResponse) : Resource<String> {

        return when(val credential = result.credential) {
            is PublicKeyCredential -> Resource.Error("Passkey Credential not implemented")
            is PasswordCredential -> Resource.Error("Password Credential not implemented")

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {

                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)


                        /**
                            // https://developer.android.com/identity/sign-in/credential-manager-siwg#create-sign
                            You can use the members of googleIdTokenCredential directly for UX
                            purposes, but don't use them to store or control access to user
                            data. For that you first need to validate the token:
                            pass googleIdTokenCredential.getIdToken() to the backend server.

                            GoogleIdTokenVerifier verifier = ... // see validation instructions
                            GoogleIdToken idToken = verifier.verify(idTokenString);

                            // To get a stable account identifier (e.g. for storing user data),
                            // use the subject ID:
                            idToken.getPayload().getSubject()


                         */

                        Resource.Success(googleIdTokenCredential.idToken)

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
}


