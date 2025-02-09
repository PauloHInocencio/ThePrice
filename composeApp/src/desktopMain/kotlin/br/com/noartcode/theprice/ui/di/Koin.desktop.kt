package br.com.noartcode.theprice.ui.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.data.local.preferences.DATA_STORE_FILE_NAME
import br.com.noartcode.theprice.data.local.preferences.createDataStore
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase() }
    single<ICurrencyFormatter> {
        CurrencyFormatter(
            symbols = DecimalFormatSymbols(Locale.getDefault())
        )
    }
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
    single<DataStore<Preferences>> { createDataStore(producePath = { DATA_STORE_FILE_NAME }) }
    single<HttpClient> { createHttpClient(OkHttp.create(), localDataSource = get()) }
}

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(platformModule(), viewModelsModule(), commonModule())
        }
    }
}