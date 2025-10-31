package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ILogoutUser {
    operator fun invoke() : Flow<Resource<Unit>>
}

class LogoutUser(
    private val authRepository: AuthRepository,
    private val dispatcher:CoroutineDispatcher,
) : ILogoutUser {
    override fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        emit(authRepository.logoutUser())
    }.flowOn(dispatcher)

}