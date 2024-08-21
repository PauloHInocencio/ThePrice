package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.helpers.BillLocalDataSourceFakeImp
import br.com.noartcode.theprice.data.localdatasource.helpers.PaymentLocalDataSourceFakeImp
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.dsl.module
import java.util.Calendar
import java.util.Locale

actual fun platformTestModule() = module {
    single<IGetMonthName>{
        GetMonthName(calendar = Calendar.getInstance(), Locale("pt", "BR"))
    }
}

actual abstract class RobolectricTests