package br.com.noartcode.theprice.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File


fun getDatabase(): ThePriceDatabase {
    val dbFile = File(System.getProperty("java.io.tmpdir"), dbFileName)
    return Room.databaseBuilder<ThePriceDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
