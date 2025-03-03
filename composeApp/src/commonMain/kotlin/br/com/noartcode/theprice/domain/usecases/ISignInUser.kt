package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.ui.presentation.auth.account.IAccountManager
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfSuccess
import br.com.noartcode.theprice.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ISignInUser {
    operator fun invoke() : Flow<Resource<Unit>>
}

class SignInUser(
    private val accountManager: IAccountManager,
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: SessionStorage,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ISignInUser {

    override fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        when(val googleResult = accountManager.singInWithGoogle()) {
            is Resource.Success -> {
                val (tokenId, rawNonce) = googleResult.data
                val signInResult = remoteDataSource.signUpUser(tokenId, rawNonce).doIfSuccess { data ->
                    localDataSource.saveUser(data.user)
                    localDataSource.saveAccessToken(data.accessToken)
                    localDataSource.saveRefreshToken(data.refreshToken)
                }
                emit(signInResult.map {  })
            }
            else ->  emit(googleResult.map {  })
        }
    }.flowOn(dispatcher)
}