package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.BillLocalDataSourceFakeImp
import br.com.noartcode.theprice.data.localdatasource.PaymentLocalDataSourceFakeImp
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.domain.usecases.InsertNewBill
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
    single<IInsertNewBill> { InsertNewBill(localDataSource = get(), dispatcher = UnconfinedTestDispatcher()) }
    viewModel { NewBillViewModel(formatter = get(), insertNewBill = get()) }
    viewModel {
        HomeViewModel(
            getTodayDay = get(),
            getPayments = get(),
            getMonthName = get(),
            getDateMonthAndYear = get()
        )
    }
}