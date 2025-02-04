package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.util.Resource

class AuthRepositoryImp(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
) : AuthRepository {
    override suspend fun singUpUser(tokenID: String, rawNonce: String): Resource<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(): Resource<User> {
        TODO("Not yet implemented")
    }
}