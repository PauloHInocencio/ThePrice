package br.com.noartcode.theprice.domain.usecases.payment

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IPostPayments {
    suspend operator fun invoke(payments:List<Payment>) : Resource<Unit>
}

class PostPayments(
    private val repository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : IPostPayments {
    override suspend fun invoke(payments:List<Payment>): Resource<Unit> = withContext(ioDispatcher){
        return@withContext when(val result = repository.post(payments)){
            is Resource.Success -> {
                repository.update(payments.map { it.copy(isSynced = true) })
                result
            }
            is Resource.Error -> {
                result.exception?.printStackTrace()
                result
            }
            is Resource.Loading -> {
                Resource.Error(message = "Invalid result while posting payments")
            }
        }
    }
}