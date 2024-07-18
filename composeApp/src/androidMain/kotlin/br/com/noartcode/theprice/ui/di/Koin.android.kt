package br.com.noartcode.theprice.ui.di

import android.content.Context
import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun platformModule() = module {
    single<ThePrinceDatabase> { getDatabase(context = get()) }
}

actual class KoinInitializer(
    private val context:Context
) {
    actual fun init() {
        startKoin {
            androidContext(context)
            androidLogger()
            modules(platformModule(), appModule())
        }
    }
}