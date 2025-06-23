package br.com.noartcode.theprice.domain.usecases.payment

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IGetOldestPaymentRecordDate {
    operator fun invoke() : Flow<DayMonthAndYear?>
}

class GetOldestPaymentRecordDate(
    private val repository: BillsRepository,
    private val epochFormatter: IEpochMillisecondsFormatter,
) : IGetOldestPaymentRecordDate {
    override fun invoke(): Flow<DayMonthAndYear?> = repository
        .getAllBills()
        .map { bills -> bills.minOfOrNull { epochFormatter.from(it.billingStartDate) }}
        .map { epoch -> epoch?.let { epochFormatter.to(it) } }
}