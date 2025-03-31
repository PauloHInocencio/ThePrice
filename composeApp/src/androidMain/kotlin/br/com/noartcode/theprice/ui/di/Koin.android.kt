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
import br.com.noartcode.theprice.data.remote.workers.AndroidSyncInitializerWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncInitializerWorker
import br.com.noartcode.theprice.data.remote.workers.SyncInitializerWorker
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import br.com.noartcode.theprice.ui.presentation.account.AccountManager
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.Calendar

actual fun platformModule() = module {
    single<ThePriceDatabase> { getDatabase(context = get()) }
    single<ICurrencyFormatter> { CurrencyFormatter(symbols = DecimalFormat().decimalFormatSymbols)}
    single<IGetMonthName> { GetMonthName(calendar = Calendar.getInstance())}
    factory<IAccountManager> { AccountManager(context = androidContext()) }
    single<DataStore<Preferences>> { createDataStore(context = get()) }
    single<HttpClient> { createHttpClient(OkHttp.create(), localDataSource = get()) }
    single<ISyncInitializerWorker> { SyncInitializerWorker(manager =  WorkManager.getInstance(androidContext()) ) }
    worker {
        AndroidSyncInitializerWorker(
            appContext = androidContext(),
            params = get(),
            billsRepository = get(),
            paymentsRepository = get()
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
            modules(platformModule(), viewModelsModule(), commonModule())
        }
    }
}