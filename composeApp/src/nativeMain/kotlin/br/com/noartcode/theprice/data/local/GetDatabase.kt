package br.com.noartcode.theprice.data.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

fun getDatabase(): ThePrinceDatabase {
    val dbFilePath = NSHomeDirectory() + dbFileName
    return Room.databaseBuilder<ThePrinceDatabase>(
        name = dbFilePath,
        factory = { ThePrinceDatabase::class.instantiateImpl() }
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

