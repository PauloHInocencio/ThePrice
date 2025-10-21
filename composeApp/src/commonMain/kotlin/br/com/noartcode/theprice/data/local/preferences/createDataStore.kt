package br.com.noartcode.theprice.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath

fun createDataStore(producePath: () -> String, scope: CoroutineScope) : DataStore<Preferences>{
    return PreferenceDataStoreFactory.createWithPath(
        scope = scope,
        produceFile = { producePath().toPath() }
    )
}

internal const val DATA_STORE_FILE_NAME = "theprice_prefs.preferences_pb"