package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.util.Resource

actual class AccountManager : IAccountManager {
    override suspend fun signInWithGoogle(): Resource<Pair<String, String>> {
        TODO("Not yet implemented")
    }
}