package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSourceImp
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSourceImp
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.EpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.GetDateFormat
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetDaysInMonth
import br.com.noartcode.theprice.domain.usecases.GetDaysUntil
import br.com.noartcode.theprice.domain.usecases.GetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.IDeleteBill
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetDaysInMonth
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IGetPaymentByID
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.IGetUserInfo
import br.com.noartcode.theprice.domain.usecases.IInsertOrReplaceBill
import br.com.noartcode.theprice.domain.usecases.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.ISignInUser
import br.com.noartcode.theprice.domain.usecases.IUpdateBill
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.IUpdatePaymentStatus
import br.com.noartcode.theprice.domain.usecases.InsertOrReplaceBill
import br.com.noartcode.theprice.domain.usecases.MoveMonth
import br.com.noartcode.theprice.domain.usecases.SignInUser
import br.com.noartcode.theprice.domain.usecases.UpdateBill
import br.com.noartcode.theprice.domain.usecases.UpdatePayment
import br.com.noartcode.theprice.domain.usecases.UpdatePaymentStatus
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.mapper.PaymentDomainToUiMapper
import br.com.noartcode.theprice.ui.presentation.home.HomeViewModel
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.bill.add.AddBillViewModel
import br.com.noartcode.theprice.ui.presentation.bill.edit.EditBillViewModel
import br.com.noartcode.theprice.ui.presentation.payment.edit.PaymentEditViewModel
import br.com.noartcode.theprice.ui.presentation.auth.account.AccountViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun commonModule() = module {
    single<IGetTodayDate> { GetTodayDate() }
    single<IGetDateFormat> { GetDateFormat() }
    single<IGetDaysUntil> { GetDaysUntil() }
    single<UiMapper<Payment, PaymentUi?>> {
        PaymentDomainToUiMapper(
            dataSource = get(),
            formatter = get(),
            getTodayDate = get(),
            dateFormat = get(),
            getDaysUntil = get()
        )
    }
    single<IGetDateMonthAndYear> { GetDateMonthAndYear() }
    single<IGetDaysInMonth> { GetDaysInMonth() }
    single<IMoveMonth> { MoveMonth() }
    single<IEpochMillisecondsFormatter> { EpochMillisecondsFormatter() }
    single<BillLocalDataSource> {
        BillLocalDataSourceImp(
            database = get(),
            epochFormatter = get(),
            getTodayDate = get()
        )
    }
    single<PaymentLocalDataSource> { PaymentLocalDataSourceImp(database = get())}
    single<AuthLocalDataSource> { AuthLocalDataSourceImp(dataStore = get()) }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImp(client = get()) }
    single<IGetBillByID> { IGetBillByID(get<BillLocalDataSource>()::getBill) }
    single<IDeleteBill> { IDeleteBill(get<BillLocalDataSource>()::delete)}
    single<IInsertOrReplaceBill> { InsertOrReplaceBill(localDataSource = get()) }
    single<IUpdateBill> { UpdateBill(localDataSource = get()) }
    single<IGetPayments> { GetPayments(billLDS = get(), paymentLDS = get())}
    single<IGetPaymentByID> { IGetPaymentByID(get<PaymentLocalDataSource>()::getPayment) }
    single<IUpdatePayment> { UpdatePayment(datasource = get(), currencyFormatter = get())}
    single<IUpdatePaymentStatus> { UpdatePaymentStatus(datasource = get()) }
    single<IGetOldestPaymentRecordDate> {
        GetOldestPaymentRecordDate(localDataSource = get(), epochFormatter = get() )
    }
    single<IGetUserInfo> { IGetUserInfo(get<AuthLocalDataSource>()::getUser) }
    single<ISignInUser> {
        SignInUser(
            accountManager = get(),
            localDataSource = get(),
            remoteDataSource = get()
        )
    }
}

fun viewModelsModule() = module {
    viewModel {
        HomeViewModel(
            getPayments = get(),
            getMonthName = get(),
            getTodayDay = get(),
            paymentUiMapper = get(),
            moveMonth = get(),
            getFirstPaymentDate = get(),
            updatePaymentStatus = get(),
        )
    }
    viewModel {
        AddBillViewModel(
            currencyFormatter = get(),
            insertNewBill = get(),
            getTodayDate = get(),
            epochFormatter = get(),
            getMonthName = get(),
        )
    }
    viewModel {
        EditBillViewModel(
            currencyFormatter = get(),
            updateBill = get(),
            getTodayDate = get(),
            epochFormatter = get(),
            getMonthName = get(),
            getBill = get(),
            deleteBill = get(),
        )
    }
    viewModel {
        PaymentEditViewModel(
            getPayment = get(),
            getBill = get(),
            epochFormatter = get(),
            dateFormatter = get(),
            currencyFormatter = get(),
            updatePayment = get(),
            paymentUiMapper = get(),
        )
    }

    viewModel {
        AccountViewModel(
            signInUser = get(),
            getUserInfo = get()
        )
    }
}