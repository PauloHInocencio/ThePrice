package br.com.noartcode.theprice

import android.app.Application
import br.com.noartcode.theprice.ui.di.KoinInitializer

class ThePriceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}