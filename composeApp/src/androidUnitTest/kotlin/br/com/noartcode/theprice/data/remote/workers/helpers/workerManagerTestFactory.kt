package br.com.noartcode.theprice.data.remote.workers.helpers

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.koin.androidx.workmanager.factory.KoinWorkerFactory
import org.koin.core.KoinApplication

fun KoinApplication.workManagerTestFactory() {
    createWorkManagerTestFactory()
}

private fun KoinApplication.createWorkManagerTestFactory() {
    val factory = DelegatingWorkerFactory()
        .apply {
            addFactory(KoinWorkerFactory())
        }
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val config = Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .setWorkerFactory(factory)
        .setExecutor(SynchronousExecutor())
        .build()

    // Initialize WorkManager for instrumentation tests.
    WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
}