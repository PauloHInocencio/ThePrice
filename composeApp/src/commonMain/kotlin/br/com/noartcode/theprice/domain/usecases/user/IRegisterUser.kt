package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface IRegisterUser {
    operator fun invoke(name:String, email:String, password: String) : Flow<Resource<User>>
}


class RegisterUser (
    private val authRepository: AuthRepository,
    private val dispatcher: CoroutineDispatcher,
) : IRegisterUser {
    override operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        emit(authRepository.createUser(name, email, password))
    }.flowOn(dispatcher)
}
