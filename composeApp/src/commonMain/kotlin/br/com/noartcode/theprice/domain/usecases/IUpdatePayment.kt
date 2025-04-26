package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IUpdatePayment {
    suspend operator fun invoke(
        payment: Payment
    ) : Resource<Unit>
}


class UpdatePayment(
    private val repository: PaymentsRepository,
    private val syncUpdatedPaymentWorker: ISyncUpdatedPaymentWorker,
    private val dispatcher: CoroutineDispatcher,
) : IUpdatePayment {
    override suspend fun invoke(
        payment: Payment
    ): Resource<Unit> = withContext(dispatcher) {
        try {
            repository.update(payment)
            syncUpdatedPaymentWorker.sync(payment.id)
            return@withContext Resource.Success(Unit)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                message = "Error when tried to update payment",
                exception = e
            )
        }
    }
}