package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.helpers.BillLocalDataSourceFakeImp
import br.com.noartcode.theprice.data.localdatasource.helpers.PaymentLocalDataSourceFakeImp
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.dsl.module
import platform.Foundation.NSCalendar
import platform.Foundation.NSLocale

actual fun platformTestModule() = module{
    single<BillLocalDataSource> { BillLocalDataSourceFakeImp() }
    single<PaymentLocalDataSource> { PaymentLocalDataSourceFakeImp() }
    single<IGetMonthName> { GetMonthName(
        calendar = NSCalendar.currentCalendar(),
        locale = NSLocale("pt_BR")
    )}
}

actual abstract class RobolectricTests