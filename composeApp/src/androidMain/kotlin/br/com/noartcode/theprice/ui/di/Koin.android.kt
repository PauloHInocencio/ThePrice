package br.com.noartcode.theprice.ui.di

import android.content.Context
import android.icu.text.DecimalFormat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.data.local.preferences.createDataStore
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncDeletedBillWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncUpdatedPaymentWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncDeletedBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.data.remote.workers.SyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncDeletedBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.SyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncUpdatedPaymentWorker
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.ui.presentation.account.AccountManager
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Calendar

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(context = androidApplication(), ioDispatcher = get()) }
    single<ICurrencyFormatter> { CurrencyFormatter(symbols = DecimalFormat().decimalFormatSymbols)}
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
    factory<IAccountManager> { AccountManager(context = androidContext()) }
    single<DataStore<Preferences>> { createDataStore(context = get()) }
    single<HttpClient> { createHttpClient(OkHttp.create(), localDataSource = get()) }
    single<ISyncPaymentsWorker> { SyncPaymentsWorker(manager = WorkManager.getInstance(androidContext())) }
    single<ISyncUpdatedPaymentWorker> { SyncUpdatedPaymentWorker(manager = WorkManager.getInstance(androidContext()))}
    single<ISyncBillWorker> { SyncBillWorker(manager = WorkManager.getInstance(androidContext())) }
    single<ISyncUpdatedBillWorker> { SyncUpdatedBillWorker(manager = WorkManager.getInstance(androidContext()))}
    single<ISyncDeletedBillWorker> { SyncDeletedBillWorker(manager = WorkManager.getInstance(androidContext()))}
    worker {
        AndroidSyncPaymentsWorker(
            appContext = androidContext(),
            params = get(),
            paymentsRepository = get(),
            ioDispatcher = get(),
        )
    }
    worker {
        AndroidSyncUpdatedPaymentWorker(
            appContext = androidContext(),
            params = get(),
            paymentsRepository = get(),
            ioDispatcher = get(),
        )
    }
    worker {
        AndroidSyncBillWorker(
            appContext = androidContext(),
            params = get(),
            billsRepository = get(),
            ioDispatcher = get(),
        )
    }
    worker {
        AndroidSyncUpdatedBillWorker(
            appContext = androidContext(),
            params = get(),
            billsRepository = get(),
            ioDispatcher = get(),
        )
    }
    worker {
        AndroidSyncDeletedBillWorker(
            appContext = androidContext(),
            params = get(),
            remoteDataSource = get(),
            ioDispatcher = get()
        )
    }
}

actual class KoinInitializer(
    private val context:Context
) {
    actual fun init() {
        startKoin {
            androidContext(context)
            androidLogger()
            workManagerFactory()
            modules(dispatcherModule(), platformModule(), viewModelsModule(), commonModule())
        }
    }
}