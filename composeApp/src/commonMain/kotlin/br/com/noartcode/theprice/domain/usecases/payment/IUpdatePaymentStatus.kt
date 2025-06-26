package br.com.noartcode.theprice.domain.usecases.payment

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext


interface IUpdatePaymentStatus {
    suspend operator fun invoke(id: String, isPayed:Boolean) : Resource<Payment>
}

class UpdatePaymentStatus (
    private val repository: PaymentsRepository,
    private val dispatcher: CoroutineDispatcher,
) : IUpdatePaymentStatus {
    override suspend fun invoke(
        id: String,
        isPayed: Boolean
    ): Resource<Payment> = withContext(dispatcher) {
        try {
            val payment = repository
                .get(id)!!
                .copy(
                    isPayed = isPayed,
                    isSynced = false,
                )

            repository.update(payment)
            return@withContext Resource.Success(payment)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                message = "Error when tried to update payment",
                exception = e
            )
        }
    }

}