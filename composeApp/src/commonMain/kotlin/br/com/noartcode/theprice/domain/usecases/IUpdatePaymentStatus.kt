package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


interface IUpdatePaymentStatus {
    suspend operator fun invoke(id: String, isPayed:Boolean) : Resource<Unit>
}

class UpdatePaymentStatus (
    private val datasource: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUpdatePaymentStatus {
    override suspend fun invoke(
        id: String,
        isPayed: Boolean
    ): Resource<Unit> = withContext(dispatcher) {
        try {
            val payment = datasource
                .getPayment(id)!!
                .copy(isPayed = isPayed)

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