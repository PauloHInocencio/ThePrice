package br.com.noartcode.theprice.ui.presentation.user.account

import br.com.noartcode.theprice.util.Resource

actual class AccountManager : IAccountManager {
    override suspend fun singInWithGoogle(): Resource<String> {
        TODO("Not yet implemented")
    }
}