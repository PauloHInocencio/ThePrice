package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfSuccess
import br.com.noartcode.theprice.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ILoginUser {
    operator fun invoke() : Flow<Resource<User?>>
}

class LoginUser(
    private val authRepository: AuthRepository,
    private val accountManager: IAccountManager,
    private val dispatcher: CoroutineDispatcher
) : ILoginUser {

    override fun invoke(): Flow<Resource<User?>> = flow {
        emit(Resource.Loading)
        when(val googleResult = accountManager.signInWithGoogle()) {
            is Resource.Success -> {
                val (tokenId, rawNonce) = googleResult.data
                emit(authRepository.singUpUser(tokenId, rawNonce))
            }
            else -> emit(googleResult.map { null })
        }
    }.flowOn(dispatcher)
}