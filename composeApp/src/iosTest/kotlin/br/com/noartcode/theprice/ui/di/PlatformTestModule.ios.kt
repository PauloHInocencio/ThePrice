package br.com.noartcode.theprice.ui.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.preferences.TEST_FILE_NAME
import br.com.noartcode.theprice.data.local.preferences.createDataStore
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import kotlinx.datetime.Clock
import org.koin.dsl.module
import platform.Foundation.NSCalendar
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual var currentTestFileName: String? = null

actual fun platformTestModule() = module{
    single<IGetMonthName> { GetMonthName(
        calendar = NSCalendar.currentCalendar(),
        locale = NSLocale("pt_BR")
    )}
    single<ICurrencyFormatter> {
        CurrencyFormatter(formatter = NSNumberFormatter().also {
            it.usesGroupingSeparator = true
            it.numberStyle = NSNumberFormatterCurrencyStyle
            it.locale = NSLocale("pt_BR")
        })
    }

    single<DataStore<Preferences>>{
        // Create a unique file for each Koin context
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val uniqueFileName = "${timestamp}_$TEST_FILE_NAME"
        currentTestFileName = uniqueFileName
        createDataStore(
            scope = get(),
            fileName = uniqueFileName,
        )
    }

    single<ThePriceDatabase> {
        Room.inMemoryDatabaseBuilder<ThePriceDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}

actual abstract class RobolectricTests