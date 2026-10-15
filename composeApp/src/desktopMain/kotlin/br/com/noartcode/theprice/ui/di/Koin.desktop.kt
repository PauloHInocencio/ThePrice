package br.com.noartcode.theprice.ui.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import br.com.noartcode.theprice.data.local.preferences.DATA_STORE_FILE_NAME
import br.com.noartcode.theprice.data.local.preferences.createDataStore
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.data.local.workers.OverduePaymentReminderWorker
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.data.remote.workers.ISyncDeletedBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.data.remote.workers.SyncDeletedBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.SyncUpdatedBillWorker
import br.com.noartcode.theprice.data.remote.workers.SyncUpdatedPaymentWorker
import br.com.noartcode.theprice.data.local.datasource.settings.AppSettingsCache
import br.com.noartcode.theprice.domain.usecases.settings.IGetAppCurrency
import br.com.noartcode.theprice.platform.locale.ILocaleManager
import br.com.noartcode.theprice.platform.locale.DesktopLocaleManager
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.ui.presentation.account.AccountManager
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Calendar

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(ioDispatcher = get()) }
    single<ICurrencyFormatter> {
        // Resolved once per process (currency changes apply after restart). Normally filled by Splash;
        // the blocking read only happens on paths that skip Splash (e.g. notification deep link).
        val currency = get<AppSettingsCache>().settings?.currency
            ?: runBlocking { get<IGetAppCurrency>().invoke().first() }
        CurrencyFormatter(currency = currency)
    }
    single<ILocaleManager> { DesktopLocaleManager() }
    single<IOverduePaymentReminderWorker> { OverduePaymentReminderWorker() }
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
    single<DataStore<Preferences>> { createDataStore(producePath = { DATA_STORE_FILE_NAME }, scope = get()) }
    single<HttpClient> { createHttpClient(OkHttp.create(), localDataSource = get()) }
    factory<IAccountManager> { AccountManager() }
    single<ISyncPaymentsWorker> { SyncPaymentsWorker(paymentsRepository = get(), ioDispatcher = get()) }
    single<ISyncUpdatedPaymentWorker> { SyncUpdatedPaymentWorker(paymentsRepository = get(), ioDispatcher = get()) }
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