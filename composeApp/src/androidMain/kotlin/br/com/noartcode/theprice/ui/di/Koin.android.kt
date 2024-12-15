package br.com.noartcode.theprice.ui.di

import android.content.Context
import android.icu.text.DecimalFormat
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Calendar

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(context = get()) }
    single<ICurrencyFormatter> { CurrencyFormatter(symbols = DecimalFormat().decimalFormatSymbols)}
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
}

actual class KoinInitializer(
    private val context:Context
) {
    actual fun init() {
        startKoin {
            androidContext(context)
            androidLogger()
            modules(platformModule(), viewModelsModule(), commonModule())
        }
    }
}