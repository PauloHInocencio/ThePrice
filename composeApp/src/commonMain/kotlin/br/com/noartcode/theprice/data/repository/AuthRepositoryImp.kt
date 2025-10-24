package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.data.remote.dtos.UserCredentialsDto
import br.com.noartcode.theprice.data.remote.mapper.toDomain
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfSuccess
import br.com.noartcode.theprice.util.map
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthRepositoryImp(
    private val remote: AuthRemoteDataSource,
    private val local: AuthLocalDataSource,
) : AuthRepository {

    override suspend fun singUpUser(tokenID: String, rawNonce: String): Resource<User> {
        val deviceID = getDeviceID()
        return remote.signUpUser(tokenID, deviceID, rawNonce).doIfSuccess { data ->
            local.saveCredentials(data)
        }.map{ credentials: UserCredentialsDto -> credentials.toDomain() }
    }


    @OptIn(ExperimentalUuidApi::class)
    private suspend fun getDeviceID(): String {
        return local.getDeviceID().first()
            ?: run { Uuid.random().toString() }
                .also { local.saveDeviceID(it) }
    }


    override suspend fun getUserInfo(): Resource<User> {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(
        name: String,
        email: String,
        password: String
    ): Resource<User> {
        val deviceID = getDeviceID()
        return when(val result = remote.createUser(name, email, password, deviceID)) {
            is Resource.Success -> {
                local.saveCredentials(result.data)
                Resource.Success(result.data.toDomain())
            }
            is Resource.Error,
            is Resource.Loading -> result
        }
    }

    override suspend fun logoutUser(): Resource<Unit> {
        val rt = local.getRefreshToken().first() ?: ""
        return when(val result = remote.logoutUser(refreshToken = rt)) {
            is Resource.Success -> {
                local.clean()
                Resource.Success(Unit)
            }
            else -> result
        }
    }
}