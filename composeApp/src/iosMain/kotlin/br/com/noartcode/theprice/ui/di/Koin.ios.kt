package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.getDatabase
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun platformModule() = module {
    single<ThePrinceDatabase> { getDatabase() }
}


actual class KoinInitializer {

    actual fun init() {
        startKoin {
            modules(platformModule(), appModule())
        }
    }
}