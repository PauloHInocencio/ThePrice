package br.com.noartcode.theprice.ui.presentation.auth.account

import br.com.noartcode.theprice.util.Resource

interface  IAccountManager {
    suspend fun singInWithGoogle() : Resource<Pair<String, String>>
}

expect class AccountManager : IAccountManager