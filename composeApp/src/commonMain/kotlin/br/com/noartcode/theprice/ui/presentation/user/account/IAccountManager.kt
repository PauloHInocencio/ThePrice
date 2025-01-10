package br.com.noartcode.theprice.ui.presentation.user.account

import br.com.noartcode.theprice.util.Resource

interface  IAccountManager {
    suspend fun singInWithGoogle() : Resource<String>
}

expect class AccountManager : IAccountManager