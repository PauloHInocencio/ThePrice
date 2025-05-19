package br.com.noartcode.theprice.ui.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.data.local.preferences.createDataStore
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSCalendar
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale


actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(ioDispatcher = get()) }
    single<ICurrencyFormatter> {
        CurrencyFormatter(formatter = NSNumberFormatter().also {
            it.usesGroupingSeparator = true
            it.numberStyle = NSNumberFormatterCurrencyStyle
            it.locale = NSLocale.currentLocale
        })
    }
    single<IGetMonthName>{ GetMonthName(calendar = NSCalendar.currentCalendar()) }
    single<DataStore<Preferences>> { createDataStore() }
    single<HttpClient> { createHttpClient(Darwin.create(), localDataSource = get()) }
}


actual class KoinInitializer {

    actual fun init() {
        startKoin {
            modules(platformModule(), viewModelsModule(), commonModule())
        }
    }
}