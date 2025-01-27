package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IUpdatePayment {
    suspend operator fun invoke(
        id: Long,
        price: String,
        dueDate: DayMonthAndYear,
        isPayed: Boolean,
    ) : Resource<Unit>
}


class UpdatePayment(
    private val datasource: PaymentLocalDataSource,
    private val currencyFormatter: ICurrencyFormatter,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IUpdatePayment {
    override suspend fun invoke(
        id: Long,
        price: String,
        dueDate: DayMonthAndYear,
        isPayed: Boolean,
    ): Resource<Unit> = withContext(dispatcher) {
        try {
            datasource.updatePayment(
                id = id,
                price = price.let { currencyFormatter.clenup(it) },
                dueDate = dueDate,
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