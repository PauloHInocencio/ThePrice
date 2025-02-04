package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.ui.presentation.auth.account.IAccountManager
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfSuccess
import br.com.noartcode.theprice.util.map

interface ISignInUser {
    suspend operator fun invoke() : Resource<Unit>
}

class SignInUser(
    private val accountManager: IAccountManager,
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) : ISignInUser {

    override suspend fun invoke(): Resource<Unit> {
        return when(val googleResult = accountManager.singInWithGoogle()) {
            is Resource.Success -> {
                val (tokenId, rawNonce) = googleResult.data
                val signInResult = remoteDataSource.signUpUser(tokenId, rawNonce).doIfSuccess { data ->
                    localDataSource.saveUser(data.user)
                    localDataSource.saveAccessToken(data.accessToken)
                    localDataSource.saveRefreshToken(data.refreshToken)
                }
                signInResult.map {  }
            }
            else ->  googleResult.map {  }
        }
    }
}