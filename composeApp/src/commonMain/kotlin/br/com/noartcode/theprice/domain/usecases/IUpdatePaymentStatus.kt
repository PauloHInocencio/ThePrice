package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


interface IUpdatePaymentStatus {
    suspend operator fun invoke(id: String, isPayed:Boolean) : Resource<Unit>
}

class UpdatePaymentStatus (
    private val repository: PaymentsRepository,
    private val syncUpdatedPaymentWorker: ISyncUpdatedPaymentWorker,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUpdatePaymentStatus {
    override suspend fun invoke(
        id: String,
        isPayed: Boolean
    ): Resource<Unit> = withContext(dispatcher) {
        try {
            val payment = repository
                .get(id)!!
                .copy(
                    isPayed = isPayed,
                    isSynced = false,
                )

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