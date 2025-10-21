package br.com.noartcode.theprice.ui.presentation.account.add

data class NewAccountUiState (
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val profileImage: String = "",
    val emailHasError: Boolean = false,
    val passwordHasError: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isCreated: Boolean = false,
) {
    val canSave: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()

}