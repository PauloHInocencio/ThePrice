package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IGetOldestPaymentRecordDate {
    operator fun invoke() : Flow<DayMonthAndYear?>
}

class GetOldestPaymentRecordDate(
    private val localDataSource: BillLocalDataSource,
    private val epochFormatter: IEpochMillisecondsFormatter,
) : IGetOldestPaymentRecordDate {
    override fun invoke(): Flow<DayMonthAndYear?> = localDataSource
        .getAllBills()
        .map { bills -> bills.minOfOrNull { epochFormatter.from(it.billingStartDate) }}
        .map { epoch -> epoch?.let { epochFormatter.to(it) } }
}