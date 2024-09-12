package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

interface IUpdatePayment {
    suspend operator fun invoke(
        id: Long,
        payedValue: String? = null,
        paidAt: DayMonthAndYear? = null
    ) : Payment?
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
    ): Payment? = withContext(dispatcher) {
        return@withContext datasource.updatePayment(
            id = id,
            paidValue = payedValue?.let { currencyFormatter.clenup(it) },
            paidAt = paidAt,
        )
    }
}