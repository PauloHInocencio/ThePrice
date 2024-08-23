package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.helpers.BillLocalDataSourceFakeImp
import br.com.noartcode.theprice.data.localdatasource.helpers.PaymentLocalDataSourceFakeImp
import br.com.noartcode.theprice.domain.usecases.GetDateFormat
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetDaysUntil
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.domain.usecases.InsertNewBill
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.presentation.home.HomeViewModel
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
fun commonTestModule() = module {
    single<BillLocalDataSource> { BillLocalDataSourceFakeImp() }
    single<PaymentLocalDataSource> { PaymentLocalDataSourceFakeImp() }
    single<IGetTodayDate> { GetTodayDate() }
    single<IGetDateFormat> { GetDateFormat() }
    single<IGetDaysUntil> { GetDaysUntil() }
    single<IGetDateMonthAndYear> { GetDateMonthAndYear() }
    single<IInsertNewBill> { InsertNewBill(localDataSource = get(), dispatcher = UnconfinedTestDispatcher()) }
    single<IGetPayments> {
        GetPayments(
            billLDS = get(),
            paymentLDS = get(),
            dispatcher = UnconfinedTestDispatcher()
        )
    }
}