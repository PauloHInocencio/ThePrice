package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSourceImp
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.local.queues.EventSyncQueueImp
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.auth.AuthRemoteDataSourceImp
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSourceImp
import br.com.noartcode.theprice.data.remote.datasource.events.EventRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.events.EventRemoteDataSourceImp
import br.com.noartcode.theprice.data.remote.datasource.payment.PaymentRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.payment.PaymentRemoteDataSourceImp
import br.com.noartcode.theprice.data.remote.workers.ISyncEventsWorker
import br.com.noartcode.theprice.data.remote.workers.SyncEventsWorker
import br.com.noartcode.theprice.data.repository.AuthRepositoryImp
import br.com.noartcode.theprice.data.repository.BillsRepositoryImp
import br.com.noartcode.theprice.data.repository.PaymentsRepositoryImp
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.EpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.GetDateFormat
import br.com.noartcode.theprice.domain.usecases.datetime.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.datetime.GetDaysInMonth
import br.com.noartcode.theprice.domain.usecases.datetime.GetDaysUntil
import br.com.noartcode.theprice.domain.usecases.GetEvent
import br.com.noartcode.theprice.domain.usecases.payment.GetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.payment.GetPayments
import br.com.noartcode.theprice.domain.usecases.datetime.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.user.GetUserData
import br.com.noartcode.theprice.domain.usecases.bill.IDeleteLocalBill
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.bill.IGetBillByID
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.datetime.IGetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.datetime.IGetDaysInMonth
import br.com.noartcode.theprice.domain.usecases.datetime.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetEvents
import br.com.noartcode.theprice.domain.usecases.payment.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.payment.IGetPaymentByID
import br.com.noartcode.theprice.domain.usecases.payment.IGetPayments
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.user.IGetUserAccountInfo
import br.com.noartcode.theprice.domain.usecases.user.IGetUserData
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBill
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.domain.usecases.bill.IInsertMissingPayments
import br.com.noartcode.theprice.domain.usecases.payment.IInsertPayments
import br.com.noartcode.theprice.domain.usecases.user.ILogoutUser
import br.com.noartcode.theprice.domain.usecases.datetime.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.user.ILoginUser
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBill
import br.com.noartcode.theprice.domain.usecases.payment.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.payment.IUpdatePaymentStatus
import br.com.noartcode.theprice.domain.usecases.bill.InsertBill
import br.com.noartcode.theprice.domain.usecases.bill.InsertBillWithPayments
import br.com.noartcode.theprice.domain.usecases.bill.InsertMissingPayments
import br.com.noartcode.theprice.domain.usecases.user.LogoutUser
import br.com.noartcode.theprice.domain.usecases.datetime.MoveMonth
import br.com.noartcode.theprice.domain.usecases.user.LoginUser
import br.com.noartcode.theprice.domain.usecases.bill.UpdateBill
import br.com.noartcode.theprice.domain.usecases.payment.UpdatePayment
import br.com.noartcode.theprice.domain.usecases.payment.UpdatePaymentStatus
import br.com.noartcode.theprice.domain.usecases.user.RegisterUser
import br.com.noartcode.theprice.domain.usecases.user.IRegisterUser
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.mapper.PaymentDomainToUiMapper
import br.com.noartcode.theprice.ui.presentation.home.HomeViewModel
import br.com.noartcode.theprice.ui.presentation.home.PaymentUi
import br.com.noartcode.theprice.ui.presentation.bill.add.AddBillViewModel
import br.com.noartcode.theprice.ui.presentation.bill.edit.EditBillViewModel
import br.com.noartcode.theprice.ui.presentation.payment.edit.EditPaymentViewModel
import br.com.noartcode.theprice.ui.presentation.account.AccountViewModel
import br.com.noartcode.theprice.ui.presentation.account.add.NewAccountViewModel
import br.com.noartcode.theprice.ui.presentation.login.LoginViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModel
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
    single<BillLocalDataSource> { BillLocalDataSourceImp(database = get()) }
    single<BillRemoteDataSource> { BillRemoteDataSourceImp(client = get())}
    single<PaymentLocalDataSource> { PaymentLocalDataSourceImp(database = get())}
    single<PaymentRemoteDataSource> { PaymentRemoteDataSourceImp(client = get(), session = get())}
    single<EventRemoteDataSource> { EventRemoteDataSourceImp(client = get(), session = get()) }
    single<AuthLocalDataSource> { AuthLocalDataSourceImp(dataStore = get(), ioDispatcher = get()) }
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImp(client = get()) }
    single<EventSyncQueue> { EventSyncQueueImp(database = get(), ioDispatcher = get()) }
    single<IGetBillByID> { IGetBillByID(get<BillLocalDataSource>()::getBill) }
    single<IDeleteLocalBill> { IDeleteLocalBill(get<BillLocalDataSource>()::delete) }
    single<IInsertBill> { InsertBill(repository = get(), worker = get(), ioDispatcher = get()) }
    single<IUpdateBill> { UpdateBill(repository = get(), dispatcher = get()) }
    single<IInsertBillWithPayments> { InsertBillWithPayments(localDataSource = get(), getTodayDate = get(), dispatcher = get()) }
    single<IInsertMissingPayments> { InsertMissingPayments(billsRepository = get(), paymentsRepository = get(), getTodayDate = get(), dispatcher = get() ) }
    single<IInsertPayments> { IInsertPayments(get<PaymentsRepository>()::insert) }
    single<IGetPayments> { GetPayments(billsRepository = get(), paymentsRepository = get(), ioDispatcher = get()) }
    single<IGetPaymentByID> { IGetPaymentByID(get<PaymentLocalDataSource>()::getPayment) }
    single<IUpdatePayment> { UpdatePayment(repository = get(), dispatcher = get()) }
    single<IUpdatePaymentStatus> { UpdatePaymentStatus(repository = get(), dispatcher = get()) }
    single<IGetOldestPaymentRecordDate> { GetOldestPaymentRecordDate(repository = get(), epochFormatter = get() ) }
    single<BillsRepository> { BillsRepositoryImp(local = get(), remote = get()) }
    single<PaymentsRepository> { PaymentsRepositoryImp(local = get(), remote = get()) }
    single<AuthRepository> { AuthRepositoryImp(local = get(), remote = get()) }
    single<IGetUserAccountInfo> { IGetUserAccountInfo(get<AuthLocalDataSource>()::getUser) }
    single<IGetUserData> { GetUserData(billsRepository = get(), paymentsRepository = get(), ioDispatcher = get()) }
    single<IGetEvents> { GetEvent(remoteDataSource = get(), dispatcher = get())}
    single<ISyncEventsWorker> {
        SyncEventsWorker(
            eventSyncQueue = get(),
            billRemoteDataSource = get(),
            paymentsDataSource = get(),
            ioDispatcher = get(),
        )
    }
    single<ILoginUser> {
        LoginUser(
            accountManager = get(),
            authRepository = get(),
            dispatcher = get(),
        )
    }
    single<ILogoutUser> {
        LogoutUser(
            authRepository = get(),
            dispatcher = get(),
        )
    }
    single<IRegisterUser> {
        RegisterUser(
            authRepository = get(),
            dispatcher = get()
        )
    }
}

fun dispatcherModule() = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    single<CoroutineScope> {
        CoroutineScope(context = get<CoroutineDispatcher>() + SupervisorJob())
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
            getEvents = get(),
            insertMissingPayments = get(),
            updateBill = get(),
            insertNewBill = get(),
            updatePayment = get(),
            insertPayments = get(),
            deleteBill = get(),
            eventSyncQueue = get(),
        )
    }
    viewModel {
        AddBillViewModel(
            currencyFormatter = get(),
            insertBillWithPayments = get(),
            getTodayDate = get(),
            epochFormatter = get(),
            getMonthName = get(),
            eventSyncQueue = get(),
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
            eventSyncQueue = get()
        )
    }
    viewModel {
        EditPaymentViewModel(
            getPayment = get(),
            getBill = get(),
            epochFormatter = get(),
            dateFormatter = get(),
            currencyFormatter = get(),
            updatePayment = get(),
            paymentUiMapper = get(),
            getTodayDate = get(),
            eventSyncQueue = get(),
        )
    }

    viewModel {
        AccountViewModel(
            signInUser = get(),
            getAccountInfo = get(),
            logOutUser = get(),
        )
    }

    viewModel {
        NewAccountViewModel()
    }

    viewModel {
        LoginViewModel(
            loginInUser = get(),
            getAccountInfo = get(),
        )
    }
}