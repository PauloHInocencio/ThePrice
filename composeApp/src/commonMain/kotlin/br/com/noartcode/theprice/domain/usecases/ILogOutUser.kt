package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ILogOutUser {
    operator fun invoke() : Flow<Resource<Unit>>
}

class LogOutUser(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: SessionStorage,
    private val dispatcher:CoroutineDispatcher = Dispatchers.IO
) : ILogOutUser {
    override fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        when(val result = remoteDataSource.logoutUser()) {
            is Resource.Success -> {
                localDataSource.clean()
                emit(Resource.Success(Unit))
            }
            else -> emit(result)
        }
    }.flowOn(dispatcher)

}