package br.com.noartcode.theprice

import android.app.Application
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class ThePriceAppTest : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
            )
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}