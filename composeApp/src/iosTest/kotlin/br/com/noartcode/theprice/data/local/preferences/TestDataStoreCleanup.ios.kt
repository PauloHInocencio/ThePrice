package br.com.noartcode.theprice.data.local.preferences

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun cleanupDataStoreFile(fileName: String) {
    val fileManager = NSFileManager.defaultManager
    val documentDirectory = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )

    val filePath = requireNotNull(documentDirectory).path + "/$fileName"
    if (fileManager.fileExistsAtPath(filePath)) {
        fileManager.removeItemAtPath(filePath, error = null)
    }
}