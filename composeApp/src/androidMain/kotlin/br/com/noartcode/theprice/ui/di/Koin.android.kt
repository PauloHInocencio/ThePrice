package br.com.noartcode.theprice.ui.di

import android.content.Context
import android.icu.text.DecimalFormat
import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.dao.BillDao
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun platformModule() = module {
    single<ThePrinceDatabase> { getDatabase(context = get()) }
    single<ICurrencyFormatter> { CurrencyFormatter(symbols = DecimalFormat().decimalFormatSymbols)}
}

actual class KoinInitializer(
    private val context:Context
) {
    actual fun init() {
        startKoin {
            androidContext(context)
            androidLogger()
            modules(platformModule(), appModule())
        }
    }
}