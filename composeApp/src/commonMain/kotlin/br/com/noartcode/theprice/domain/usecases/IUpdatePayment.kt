package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Payment
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
    private val datasource: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUpdatePayment {
    override suspend fun invoke(
        payment: Payment
    ): Resource<Unit> = withContext(dispatcher) {
        try {
            datasource.update(payment)
            return@withContext Resource.Success(Unit)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                message = "Error when tried to update payment",
                exception = e
            )
        }
    }
}