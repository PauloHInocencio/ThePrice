package br.com.noartcode.theprice.ui.presentation.account

import br.com.noartcode.theprice.util.Resource

interface  IAccountManager {
    suspend fun signInWithGoogle() : Resource<Pair<String, String>>
}

expect class AccountManager : IAccountManager