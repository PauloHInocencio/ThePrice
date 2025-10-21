package br.com.noartcode.theprice.data.local.preferences

import java.io.File

actual fun cleanupDataStoreFile(fileName: String) {
    val file = File(fileName)
    if (file.exists()) {
        file.delete()
    }
}