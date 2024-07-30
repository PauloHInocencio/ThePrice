package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.repository.BillsRepositoryImp
import br.com.noartcode.theprice.data.repository.PaymentRepositoryImp
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentRepository
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.domain.usecases.InsertNewBill
import br.com.noartcode.theprice.ui.presentation.home.PaymentsViewModel
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
    single<IGetTodayDate> { GetTodayDate() }
    single<IGetDateMonthAndYear> { GetDateMonthAndYear() }
    single<BillLocalDataSource> { BillLocalDataSourceImp(database = get())}
    single<IInsertNewBill> { InsertNewBill(localDataSource = get()) }
    //single<BillsRepository> { BillsRepositoryImp(database = get())}
    //single<PaymentRepository> { PaymentRepositoryImp(database = get())}
    viewModel { PaymentsViewModel() }
    viewModel { NewBillViewModel(formatter = get(), insertNewBill = get()) }
}