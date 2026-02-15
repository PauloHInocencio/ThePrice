package br.com.noartcode.theprice.ui.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
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
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.ui.presentation.account.AccountManager
import br.com.noartcode.theprice.ui.presentation.account.GoogleSignInProvider
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSCalendar
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale


actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(ioDispatcher = get()) }
    single<ICurrencyFormatter> {
        CurrencyFormatter(formatter = NSNumberFormatter().also {
            it.usesGroupingSeparator = true
            it.numberStyle = NSNumberFormatterCurrencyStyle
            it.locale = NSLocale.currentLocale
        })
    }
    single<IOverduePaymentReminderWorker> { OverduePaymentReminderWorker() }
    single<IGetMonthName>{ GetMonthName(calendar = NSCalendar.currentCalendar()) }
    single<DataStore<Preferences>> { createDataStore(scope = get()) }
    single<HttpClient> { createHttpClient(Darwin.create(), localDataSource = get()) }
    factory<IAccountManager> { AccountManager(signInProvider = get()) }
    single<ISyncPaymentsWorker> { SyncPaymentsWorker() }
    single<ISyncUpdatedPaymentWorker> { SyncUpdatedPaymentWorker()}
    single<ISyncUpdatedBillWorker> { SyncUpdatedBillWorker()}
    single<ISyncDeletedBillWorker> { SyncDeletedBillWorker() }
}


actual class KoinInitializer(
    private val signInProvider: GoogleSignInProvider
) {
    actual fun init() {
        startKoin {
            modules(
                module { single<GoogleSignInProvider> { signInProvider } },
                dispatcherModule(),
                platformModule(),
                viewModelsModule(),
                commonModule()
            )
        }
    }
}