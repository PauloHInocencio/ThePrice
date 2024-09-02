package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IGetFirstPaymentRecordDate {
    operator fun invoke() : Flow<DayMonthAndYear?>
}

class GetFirstPaymentRecordDate(
    private val localDataSource: BillLocalDataSource,
    private val epochFormatter: IEpochMillisecondsFormatter,
) : IGetFirstPaymentRecordDate {
    override fun invoke(): Flow<DayMonthAndYear?> = localDataSource
        .getAllBills()
        .map { bills -> bills.minOfOrNull { epochFormatter.from(it.createAt) }}
        .map { epoch -> epoch?.let { epochFormatter.to(it) } }
}