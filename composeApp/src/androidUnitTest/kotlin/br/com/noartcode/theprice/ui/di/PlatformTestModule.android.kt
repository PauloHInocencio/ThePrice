

package br.com.noartcode.theprice.ui.di

import android.content.Context
import kotlin.run
import android.icu.text.DecimalFormatSymbols
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import io.mockk.every
import io.mockk.mockk
import org.koin.dsl.module
import java.util.Calendar
import java.util.Locale
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


actual fun platformTestModule() = module {
    single<ThePrinceDatabase> {
        run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.databaseBuilder(
                context,
                ThePrinceDatabase::class.java,
                "test_db"
            ).build()
        }
    }
    single<IGetMonthName>{ GetMonthName(calendar = Calendar.getInstance(), Locale("pt", "BR"))}
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

@RunWith(RobolectricTestRunner::class)
actual abstract class RobolectricTests