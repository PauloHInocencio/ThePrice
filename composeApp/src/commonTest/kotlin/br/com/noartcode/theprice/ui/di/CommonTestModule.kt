package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSourceImp
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.data.remote.workers.ISyncBillWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope

import org.koin.dsl.module

fun commonTestModule() = module {
    single<HttpClient> {
        createHttpClient(ThePriceApiMock.engine, localDataSource = get())
    }
    single<ISyncBillWorker> {
        mock<ISyncBillWorker>().apply {
            every { this@apply.sync(billID = any()) } returns Unit
        }
    }
    single<ISyncPaymentsWorker> {
        mock<ISyncPaymentsWorker>(MockMode.autoUnit)
    }

    single<ISyncUpdatedPaymentWorker> {
        mock<ISyncUpdatedPaymentWorker>().apply {
            every { this@apply.sync(paymentID = any()) } returns Unit
        }
    }

    single<AuthLocalDataSource> {
        AuthLocalDataSourceImp(dataStore = get(), ioDispatcher = get())
    }

    single<IGetTodayDate> { GetTodayDateStub() }
}

fun dispatcherTestModule() = module {
    single<CoroutineDispatcher> { StandardTestDispatcher() }
    single<CoroutineScope> { TestScope(get<CoroutineDispatcher>()) }
}