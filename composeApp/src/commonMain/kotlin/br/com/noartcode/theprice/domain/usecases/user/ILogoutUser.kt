package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ILogoutUser {
    // force = true skips the server call (e.g. offline). The server's refresh token stays valid until it expires.
    operator fun invoke(force: Boolean = false) : Flow<Resource<Unit>>
}

class LogoutUser(
    private val authRepository: AuthRepository,
    private val clearUserSession: IClearUserSession,
    private val dispatcher: CoroutineDispatcher,
) : ILogoutUser {
    override fun invoke(force: Boolean): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)

        if (!force) {
            val result = authRepository.logoutUser()
            if (result !is Resource.Success) {
                emit(result)
                return@flow
            }
        }
        clearUserSession()
        emit(Resource.Success(Unit))
    }.flowOn(dispatcher)
}
