package br.com.noartcode.theprice.ui.di

import android.icu.text.DecimalFormatSymbols
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.ThePriceDatabaseConstructor
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import io.mockk.every
import io.mockk.mockk
import org.koin.dsl.module
import java.util.Calendar
import java.util.Locale

actual fun platformTestModule() = module {
    single<ThePriceDatabase> {
        run {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val file = instrumentation.targetContext.getDatabasePath("test.db")
            Room.databaseBuilder(
                context = instrumentation.targetContext,
                name = file.path,
                factory = ThePriceDatabaseConstructor::initialize
            ).build()
        }
    }
    single<IGetMonthName>{ GetMonthName(calendar = Calendar.getInstance(), Locale("pt", "BR")) }
    single<ICurrencyFormatter> {
        CurrencyFormatter(
            symbols = lazy {
                val mock = mockk<DecimalFormatSymbols>(relaxed = true)
                every { mock.decimalSeparator } returns ','
                every { mock.currencySymbol } returns  "R$"
                every { mock.groupingSeparator} returns '.'
                every { mock.zeroDigit } returns '0'
                mock
            }.value
        )
    }

}

actual abstract class RobolectricTests