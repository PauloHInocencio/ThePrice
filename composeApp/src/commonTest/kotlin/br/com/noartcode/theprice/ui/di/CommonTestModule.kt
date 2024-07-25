package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.BillLocalDataSourceFakeImp
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.domain.usecases.InsertNewBill
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
fun commonTestModule() = module {
    single<BillLocalDataSource> { BillLocalDataSourceFakeImp() }
    single<IInsertNewBill> { InsertNewBill(localDataSource = get(), dispatcher = UnconfinedTestDispatcher()) }
    viewModel { NewBillViewModel(formatter = get(), insertNewBill = get()) }
}