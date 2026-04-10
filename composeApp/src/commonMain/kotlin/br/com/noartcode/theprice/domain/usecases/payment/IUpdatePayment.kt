package br.com.noartcode.theprice.domain.usecases.payment

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IUpdatePayment {
    suspend operator fun invoke(
        payment: Payment
    ) : Resource<Payment>
}


class UpdatePayment(
    private val repository: PaymentsRepository,
    private val dispatcher: CoroutineDispatcher,
    private val getTodayDate: IGetTodayDate,
) : IUpdatePayment {
    override suspend fun invoke(
        payment: Payment
    ): Resource<Payment> = withContext(dispatcher) {
        try {
            val updatedPayment = payment.copy(
                updatedAt = getTodayDate().toEpochMilliseconds(),
                isSynced = false
            )
            repository.update(updatedPayment)
            return@withContext Resource.Success(updatedPayment)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                message = "Error when tried to update payment",
                exception = e
            )
        }
    }
}