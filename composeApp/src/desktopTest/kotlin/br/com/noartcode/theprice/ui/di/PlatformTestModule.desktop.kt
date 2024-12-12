package br.com.noartcode.theprice.ui.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.dsl.module
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

actual fun platformTestModule() = module {
    single<IGetMonthName>{
        GetMonthName(calendar = Calendar.getInstance(), Locale("pt", "BR"))
    }

    single<ICurrencyFormatter> {
        CurrencyFormatter(
            symbols = DecimalFormatSymbols(Locale("pt", "BR"))
        )
    }

    single<ThePriceDatabase> {
        Room.inMemoryDatabaseBuilder<ThePriceDatabase>()
            .setDriver(BundledSQLiteDriver())
            //.setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

actual abstract class RobolectricTests