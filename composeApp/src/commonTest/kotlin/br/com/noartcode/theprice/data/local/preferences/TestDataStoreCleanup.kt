package br.com.noartcode.theprice.data.local.preferences

const val TEST_FILE_NAME = "theprice_test_prefs.preferences_pb"

expect fun cleanupDataStoreFile(fileName: String = TEST_FILE_NAME)