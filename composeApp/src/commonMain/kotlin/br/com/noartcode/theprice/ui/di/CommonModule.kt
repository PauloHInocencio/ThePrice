package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.domain.usecases.InsertNewBill
import br.com.noartcode.theprice.ui.presentation.home.HomeViewModel
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    single<IGetTodayDate> { GetTodayDate() }
    single<IGetDateMonthAndYear> { GetDateMonthAndYear() }
    single<BillLocalDataSource> { BillLocalDataSourceImp(database = get()) }
    single<PaymentLocalDataSource> { PaymentLocalDataSourceImp(database = get())}
    single<IInsertNewBill> { InsertNewBill(localDataSource = get()) }
    single<IGetPayments> { GetPayments(billLDS = get(), paymentLDS = get())}
    viewModel {
        HomeViewModel(
            getPayments = get(),
            getMonthName = get(),
            getDateMonthAndYear = get(),
            getTodayDay = get()
        )
    }
    viewModel { NewBillViewModel(formatter = get(), insertNewBill = get()) }
}