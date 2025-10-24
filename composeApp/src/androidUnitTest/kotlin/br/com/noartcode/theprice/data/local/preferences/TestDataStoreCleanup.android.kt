package br.com.noartcode.theprice.data.local.preferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider

actual fun cleanupDataStoreFile(fileName: String) {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val datastoreFile = context.filesDir.resolve(fileName)
    if (datastoreFile.exists()){
        datastoreFile.delete()
    }
}