package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext


interface IUpdatePaymentStatus {
    suspend operator fun invoke(id: Long, isPayed:Boolean) : Resource<Unit>
}

class UpdatePaymentStatus (
    private val datasource: PaymentLocalDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUpdatePaymentStatus {
    override suspend fun invoke(
        id: Long,
        isPayed: Boolean
    ): Resource<Unit> = withContext(dispatcher) {
        try {
            val originalPayment = datasource.getPayment(id)!!
            datasource.updatePayment(
                id = id,
                price = originalPayment.price,
                dueDate = originalPayment.dueDate,
                isPayed = isPayed,
            )
            return@withContext Resource.Success(Unit)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                message = "Error when tried to update payment",
                exception = e
            )
        }
    }

}