

package br.com.noartcode.theprice.ui.di

import android.content.Context
import android.icu.text.DecimalFormatSymbols
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkManager
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncUpdatedPaymentWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.data.remote.workers.SyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.SyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncUpdatedPaymentWorker
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

    single<ISyncUpdatedBillWorker> {
        SyncUpdatedBillWorker(manager = get())
    }

    single<ISyncUpdatedPaymentWorker> {
        SyncUpdatedPaymentWorker(manager = get())
    }

    single<ISyncPaymentsWorker> {
        SyncPaymentsWorker(manager = get())
    }

    worker {
        AndroidSyncBillWorker(
            appContext = ApplicationProvider.getApplicationContext(),
            params = get(),
            billsRepository = get(),
            ioDispatcher = get(),
        )
    }

    worker {
        AndroidSyncUpdatedBillWorker(
            appContext = ApplicationProvider.getApplicationContext(),
            params = get(),
            billsRepository = get(),
            ioDispatcher = get(),
        )
    }

    worker {
        AndroidSyncUpdatedPaymentWorker(
            appContext = ApplicationProvider.getApplicationContext(),
            params = get(),
            paymentsRepository = get(),
            ioDispatcher = get(),
        )
    }

    worker {
        AndroidSyncPaymentsWorker(
            appContext = ApplicationProvider.getApplicationContext(),
            params = get(),
            paymentsRepository = get(),
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