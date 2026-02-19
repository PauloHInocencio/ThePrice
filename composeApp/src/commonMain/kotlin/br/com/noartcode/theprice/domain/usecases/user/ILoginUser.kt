package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.data.remote.mapper.toDomain
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import br.com.noartcode.theprice.util.Resource
import br.com.noartcode.theprice.util.Resource.*
import br.com.noartcode.theprice.util.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ILoginUser {
    operator fun invoke() : Flow<Resource<Unit>>
}

class LoginUser(
    private val authRepository: AuthRepository,
    private val billRemoteDS: BillRemoteDataSource,
    private val insertBillWithPayments: IInsertBillWithPayments,
    private val accountManager: IAccountManager,
    private val dispatcher: CoroutineDispatcher
) : ILoginUser {
    override fun invoke(): Flow<Resource<Unit>> = flow {
        emit(value = Loading)
        val googleAuth = accountManager.signInWithGoogle()
        if (googleAuth !is Success) {
            emit(googleAuth.map { Unit })
            return@flow
        }

        val (tokenId, rawNonce) = googleAuth.data
        val apiAuthResult = authRepository.signUpUser(tokenId, rawNonce)
        if (apiAuthResult !is Success) {
            emit(apiAuthResult.map { Unit })
            return@flow
        }

        val result = billRemoteDS.fetchAllBillsWithPayments()
        if (result is Error) {
            emit(Error(message = result.message, exception = result.exception))
            return@flow
        }

       if (result is Success){
            for((bill, payment) in result.data){
                insertBillWithPayments(
                    bill = bill.toDomain(),
                    payments = payment.toDomain()
                )
            }
       }

        emit(Success(data = Unit))
    }.flowOn(dispatcher)
}