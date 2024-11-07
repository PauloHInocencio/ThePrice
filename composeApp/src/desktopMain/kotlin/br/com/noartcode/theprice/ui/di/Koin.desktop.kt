package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Calendar

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase() }
    //TODO("Implement the CurrencyFormatter for this target")
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
}

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(platformModule(), appModule())
        }
    }
}