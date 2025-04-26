

package br.com.noartcode.theprice.ui.di

import android.content.Context
import android.icu.text.DecimalFormatSymbols
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncBillWorker
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import io.mockk.every
import io.mockk.mockk
import org.junit.runner.RunWith
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.Locale


actual fun platformTestModule() = module {
    single<ThePriceDatabase> {
        run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                ThePriceDatabase::class.java
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

    single<WorkManager> {
        WorkManager.getInstance(ApplicationProvider.getApplicationContext())
    }

    single<ISyncBillWorker> {
        SyncBillWorker(manager = get())
    }


    worker {
        AndroidSyncBillWorker(
            appContext = ApplicationProvider.getApplicationContext(),
            params = get(),
            billsRepository = get(),
            ioDispatcher = get(),
        )
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest= Config.NONE,
    application = ThePriceAppTest::class
)
actual abstract class RobolectricTests