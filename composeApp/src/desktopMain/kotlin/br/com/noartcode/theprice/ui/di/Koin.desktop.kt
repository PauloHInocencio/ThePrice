package br.com.noartcode.theprice.ui.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.data.local.preferences.DATA_STORE_FILE_NAME
import br.com.noartcode.theprice.data.local.preferences.createDataStore
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
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
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(ioDispatcher = get()) }
    single<ICurrencyFormatter> {
        CurrencyFormatter(
            symbols = DecimalFormatSymbols(Locale.getDefault())
        )
    }
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
    single<DataStore<Preferences>> { createDataStore(producePath = { DATA_STORE_FILE_NAME }, scope = get()) }
    single<HttpClient> { createHttpClient(OkHttp.create(), localDataSource = get()) }
    factory<IAccountManager> { AccountManager() }
    single<ISyncPaymentsWorker> { SyncPaymentsWorker(paymentsRepository = get(), ioDispatcher = get()) }
    single<ISyncUpdatedPaymentWorker> { SyncUpdatedPaymentWorker(paymentsRepository = get(), ioDispatcher = get()) }
    single<ISyncBillWorker> { SyncBillWorker(billsRepository = get(), ioDispatcher = get()) }
    single<ISyncUpdatedBillWorker> { SyncUpdatedBillWorker(billsRepository = get(), ioDispatcher = get()) }
    single<ISyncDeletedBillWorker> { SyncDeletedBillWorker(remoteDataSource = get(), ioDispatcher = get())}
}

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(dispatcherModule(),  platformModule(), viewModelsModule(), commonModule())
        }
    }
}