package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IUpdatePayment {
    suspend operator fun invoke(
        id: Long,
        payedValue: String? = null,
        paidAt: DayMonthAndYear? = null
    ) : Resource<Payment>
}


class UpdatePayment(
    private val datasource: PaymentLocalDataSource,
    private val currencyFormatter: ICurrencyFormatter,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUpdatePayment {
    override suspend fun invoke(
        id: Long,
        payedValue: String?,
        paidAt: DayMonthAndYear?
    ): Resource<Payment> = withContext(dispatcher) {
        try {
            val payment = datasource.updatePayment(
                id = id,
                paidValue = payedValue?.let { currencyFormatter.clenup(it) },
                paidAt = paidAt,
            )
            return@withContext Resource.Success(data = payment!!)
        } catch (e:Throwable) {
            return@withContext Resource.Error(
                message = "Error when tried to update payment",
                exception = e
            )
        }
    }
}