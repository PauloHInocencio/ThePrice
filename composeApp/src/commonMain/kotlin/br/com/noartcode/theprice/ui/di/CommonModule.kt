package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.EpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.GetDateFormat
import br.com.noartcode.theprice.domain.usecases.GetDateMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetDaysInMonth
import br.com.noartcode.theprice.domain.usecases.GetDaysUntil
import br.com.noartcode.theprice.domain.usecases.GetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
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
import br.com.noartcode.theprice.domain.usecases.IInsertNewBill
import br.com.noartcode.theprice.domain.usecases.IMoveMonth
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.InsertNewBill
import br.com.noartcode.theprice.domain.usecases.MoveMonth
import br.com.noartcode.theprice.domain.usecases.UpdatePayment
import br.com.noartcode.theprice.ui.mapper.UiMapper
import br.com.noartcode.theprice.ui.mapper.PaymentDomainToUiMapper
import br.com.noartcode.theprice.ui.presentation.home.HomeViewModel
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import br.com.noartcode.theprice.ui.presentation.payment.edit.PaymentEditViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun appModule() = module {
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
    single<BillLocalDataSource> { BillLocalDataSourceImp(database = get(), epochFormatter = get()) }
    single<IGetBillByID> { IGetBillByID(get<BillLocalDataSource>()::getBill) }
    single<PaymentLocalDataSource> { PaymentLocalDataSourceImp(database = get())}
    single<IInsertNewBill> { InsertNewBill(localDataSource = get()) }
    single<IGetPayments> { GetPayments(billLDS = get(), paymentLDS = get())}
    single<IGetPaymentByID> { IGetPaymentByID(get<PaymentLocalDataSource>()::getPayment) }
    single<IUpdatePayment> { UpdatePayment(datasource = get(), currencyFormatter = get())}
    single<IGetOldestPaymentRecordDate> {
        GetOldestPaymentRecordDate(localDataSource = get(), epochFormatter = get() )
    }
    viewModel {
        HomeViewModel(
            getPayments = get(),
            getMonthName = get(),
            getTodayDay = get(),
            paymentUiMapper = get(),
            moveMonth = get(),
            getFirstPaymentDate = get(),
            updatePayment = get(),
        )
    }
    viewModel {
        NewBillViewModel(
            currencyFormatter = get(),
            insertNewBill = get(),
            getTodayDate = get(),
            epochFormatter = get(),
            getMonthName = get(),
        )
    }
    viewModel {
        PaymentEditViewModel(
            getPayment = get(),
            getBill = get(),
            epochFormatter = get(),
            dateFormatter = get(),
            currencyFormatter = get(),
        )
    }
}