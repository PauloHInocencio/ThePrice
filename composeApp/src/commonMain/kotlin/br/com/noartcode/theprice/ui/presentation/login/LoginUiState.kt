package br.com.noartcode.theprice.ui.presentation.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailHasError: Boolean = false,
    val passwordHasError: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticatingWithPassword: Boolean = false,
    val isAuthenticatingWithGoogle: Boolean = false,
    val isAuthenticated: Boolean = false
) {
    val canLogin: Boolean
        get() =  !emailHasError && !passwordHasError && email.isNotBlank() && password.isNotBlank()

    val shouldEnableActions: Boolean
        get() = errorMessage == null &&
                isAuthenticatingWithGoogle.not() && isAuthenticatingWithPassword.not()
}