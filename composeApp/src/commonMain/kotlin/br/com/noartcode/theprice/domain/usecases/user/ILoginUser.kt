package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
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
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
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

        val billsResult = billsRepository.fetchAllBills()
        if (billsResult !is Success) {
            emit(billsResult)
            return@flow
        }

        val paymentsResult = paymentsRepository.fetchAllPayments()
        if (paymentsResult !is Success){
            emit(paymentsResult)
            return@flow
        }

        emit(Success(data = Unit))
    }.flowOn(dispatcher)
}