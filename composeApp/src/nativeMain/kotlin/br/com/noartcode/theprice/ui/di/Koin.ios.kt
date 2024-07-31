package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import platform.Foundation.NSCalendar
import org.koin.core.context.startKoin
import org.koin.dsl.module


actual fun platformModule() = module {
    single<ThePrinceDatabase> { getDatabase() }
    //TODO("Implement the CurrencyFormatter for this target")
    single<IGetMonthName>{ GetMonthName(calendar = NSCalendar.currentCalendar()) }
}


actual class KoinInitializer {

    actual fun init() {
        startKoin {
            modules(platformModule(), appModule())
        }
    }
}