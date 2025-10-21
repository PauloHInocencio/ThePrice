package br.com.noartcode.theprice.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope

fun createDataStore(
    context:Context,
    scope: CoroutineScope,
    fileName: String = DATA_STORE_FILE_NAME
) : DataStore<Preferences> {
    return createDataStore (
        scope = scope,
        producePath = {
            context.filesDir.resolve(fileName).absolutePath
        }
    )
}