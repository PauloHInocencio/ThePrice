package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.doIfSuccess
import br.com.noartcode.theprice.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ILoginUser {
    operator fun invoke() : Flow<Resource<Unit>>
}

@OptIn(ExperimentalUuidApi::class)
class LoginUser(
    private val accountManager: IAccountManager,
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: SessionStorage,
    private val getUserDate: IGetUserData,
    private val dispatcher: CoroutineDispatcher,
) : ILoginUser {

    override fun invoke(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        when(val googleResult = accountManager.signInWithGoogle()) {
            is Resource.Success -> {
                val (tokenId, rawNonce) = googleResult.data

                val deviceID = localDataSource.getDeviceID().first()
                    ?: run { Uuid.random().toString() }
                        .also { localDataSource.saveDeviceID(it) }


                val signInResult = remoteDataSource.signUpUser(tokenId, deviceID, rawNonce).doIfSuccess { data ->
                    localDataSource.saveUser(data.user)
                    localDataSource.saveAccessToken(data.accessToken)
                    localDataSource.saveRefreshToken(data.refreshToken)
                    getUserDate()
                }
                emit(signInResult.map {  })
            }
            else ->  emit(googleResult.map {  })
        }
    }.flowOn(dispatcher)
}