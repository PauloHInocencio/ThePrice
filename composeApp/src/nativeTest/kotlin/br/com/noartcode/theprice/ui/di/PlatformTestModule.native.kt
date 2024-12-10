package br.com.noartcode.theprice.ui.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.dsl.module
import platform.Foundation.NSCalendar
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale

actual fun platformTestModule() = module{
    single<IGetMonthName> { GetMonthName(
        calendar = NSCalendar.currentCalendar(),
        locale = NSLocale("pt_BR")
    )}
    single<ICurrencyFormatter> {
        CurrencyFormatter(formatter = NSNumberFormatter().also {
            it.usesGroupingSeparator = true
            it.numberStyle = NSNumberFormatterCurrencyStyle
            it.locale = NSLocale("pt_BR")
        })
    }

    single<ThePriceDatabase> {
        Room.inMemoryDatabaseBuilder<ThePriceDatabase>()
            .setDriver(BundledSQLiteDriver())
            //.setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

actual abstract class RobolectricTests