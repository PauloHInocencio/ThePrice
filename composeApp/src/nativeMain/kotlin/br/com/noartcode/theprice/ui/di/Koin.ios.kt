package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import platform.Foundation.NSCalendar
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale


actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase() }
    single<ICurrencyFormatter> {
        CurrencyFormatter(formatter = NSNumberFormatter().also {
            it.usesGroupingSeparator = true
            it.numberStyle = NSNumberFormatterCurrencyStyle
            it.locale = NSLocale.currentLocale
        })
    }
    single<IGetMonthName>{ GetMonthName(calendar = NSCalendar.currentCalendar()) }
}


actual class KoinInitializer {

    actual fun init() {
        startKoin {
            modules(platformModule(), appModule())
        }
    }
}