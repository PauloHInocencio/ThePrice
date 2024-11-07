package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.helpers.BillLocalDataSourceFakeImp
import br.com.noartcode.theprice.data.localdatasource.helpers.PaymentLocalDataSourceFakeImp
import br.com.noartcode.theprice.domain.usecases.EpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.GetDateFormat
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetDaysUntil
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertOrReplaceBill
import br.com.noartcode.theprice.domain.usecases.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.InsertOrReplaceBill
import br.com.noartcode.theprice.domain.usecases.MoveMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
fun commonTestModule() = module {
    single<BillLocalDataSource> { BillLocalDataSourceFakeImp() }
    single<PaymentLocalDataSource> { PaymentLocalDataSourceFakeImp() }
    single<IGetTodayDate> { GetTodayDate() }
    single<IGetDateFormat> { GetDateFormat() }
    single<IGetDaysUntil> { GetDaysUntil() }
    single<IMoveMonth> { MoveMonth() }
    single<IEpochMillisecondsFormatter> { EpochMillisecondsFormatter()}
    single<IGetDateMonthAndYear> { GetDateMonthAndYear() }
    single<IInsertOrReplaceBill> { InsertOrReplaceBill(localDataSource = get(), dispatcher = UnconfinedTestDispatcher()) }
    single<IGetPayments> {
        GetPayments(
            billLDS = get(),
            paymentLDS = get(),
            dispatcher = UnconfinedTestDispatcher()
        )
    }
}