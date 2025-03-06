package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock
import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.IGetPayments
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

import org.koin.dsl.module

fun commonTestModule(testDispatcher: TestDispatcher = StandardTestDispatcher()) = module {
    single<HttpClient> {
        createHttpClient(ThePriceApiMock.engine, localDataSource = get())
    }
    single<SessionStorage> {
        mock<SessionStorage>().apply {
            every { this@apply.getAccessToken() } returns flowOf("access_token")
        }
    }
    single<IGetTodayDate> { GetTodayDateStub() }
    single<IGetPayments> { GetPayments(billsRepository = get(), paymentsRepository = get(), insertMissingPayments = get(), dispatcher = testDispatcher)}
}