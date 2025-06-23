package br.com.noartcode.theprice.domain.usecases.payment

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IPutPayment {
    suspend operator fun invoke(payment:Payment) : Resource<Unit>
}

class PutPayment(
    private val repository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : IPutPayment {
    override suspend fun invoke(payment: Payment): Resource<Unit> = withContext(ioDispatcher) {
        return@withContext when(val result = repository.put(payment)){
            is Resource.Success -> {
                repository.update(payment.copy(isSynced = true))
                result
            }
            is Resource.Error -> {
                result.exception?.printStackTrace()
                result
            }
            Resource.Loading -> Resource.Error(message = "Invalid result while putting payment")
        }
    }
}