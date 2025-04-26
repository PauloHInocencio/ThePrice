package br.com.noartcode.theprice.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

fun getDatabase(context:Context, ioDispatcher:CoroutineDispatcher) : ThePriceDatabase {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(dbFileName)
    return Room.databaseBuilder<ThePriceDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
        .setDriver(AndroidSQLiteDriver())
        .setQueryCoroutineContext(ioDispatcher)
        .build()
}