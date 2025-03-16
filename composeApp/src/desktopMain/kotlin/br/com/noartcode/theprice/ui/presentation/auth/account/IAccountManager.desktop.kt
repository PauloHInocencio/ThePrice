package br.com.noartcode.theprice.ui.presentation.auth.account

import br.com.noartcode.theprice.util.Resource

actual class AccountManager : IAccountManager {
    override suspend fun singInWithGoogle(): Resource<Pair<String, String>> {
        TODO("Not yet implemented")
    }
}