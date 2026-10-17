package br.com.noartcode.theprice.ui.di

import androidx.lifecycle.SavedStateHandle
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.data.remote.networking.createHttpClient
import br.com.noartcode.theprice.data.remote.workers.ISyncPaymentsWorker
import br.com.noartcode.theprice.data.remote.workers.ISyncUpdatedPaymentWorker
import br.com.noartcode.theprice.domain.model.DataError
import br.com.noartcode.theprice.domain.model.SyncEvent
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBillAndApplyToPayments
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.domain.usecases.user.ILoginUser
import br.com.noartcode.theprice.platform.locale.ILocaleManager
import br.com.noartcode.theprice.ui.presentation.account.IAccountManager
import br.com.noartcode.theprice.util.Resource
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import org.koin.core.context.loadKoinModules

import org.koin.dsl.module

fun commonTestModule() = module {
    single<HttpClient> {
        createHttpClient(ThePriceApiMock.engine, localDataSource = get(), sessionEvents = get())
    }
    single<ISyncPaymentsWorker> {
        mock<ISyncPaymentsWorker>(MockMode.autoUnit)
    }

    single<ISyncUpdatedPaymentWorker> {
        mock<ISyncUpdatedPaymentWorker>().apply {
            every { this@apply.sync(paymentID = any()) } returns Unit
        }
    }

    single<IAccountManager> {
        mock<IAccountManager>().apply {
            everySuspend {
                this@apply.signInWithGoogle()
            } returns Resource.Success(data = "mockTokenId" to "mockRawNonce")
        }
    }

    single<ILocaleManager> { mock<ILocaleManager>(MockMode.autoUnit) }

    single<IGetTodayDate> { GetTodayDateStub() }
}

fun overdueReminderWorkerMockModule() = module {
    single<IOverduePaymentReminderWorker> {
        mock<IOverduePaymentReminderWorker>(MockMode.autoUnit)
    }
}

fun loginUserMockModule(result: Flow<Resource<Unit>>) = module {
    single<ILoginUser> {
        mock<ILoginUser>().apply {
            every { this@apply.invoke() } returns result
        }
    }
}

fun syncEventsUnauthorizedModule(event: SyncEvent) = module {
    single<EventSyncQueue> {
        mock<EventSyncQueue>().apply {
            every { this@apply.isEmpty } returns flowOf(false)
            everySuspend { this@apply.peek() } returns event
        }
    }
    single<BillRemoteDataSource> {
        mock<BillRemoteDataSource>().apply {
            everySuspend { this@apply.delete(any()) } returns Resource.Error(
                message = "token is expired",
                code = 401,
                exception = DataError.Unauthorized,
            )
        }
    }
}

fun updateBillAndApplyToPaymentsErrorModule() = module {
    single<IUpdateBillAndApplyToPayments> {
        mock<IUpdateBillAndApplyToPayments>().apply {
            everySuspend {
                this@apply.invoke(any(), any())
            } calls {
                delay(500)
                Resource.Error(
                    message = "An error occurred when trying to update bill with payments",
                    exception = Exception("Database error")
                )
            }
        }
    }
}

private fun savedHandleStateModule(handle: SavedStateHandle = SavedStateHandle()) = module {
    single<SavedStateHandle> { handle }
}

fun useSavedStateHandle(vararg pairs: Pair<String, Any?>) {
   val handle = SavedStateHandle().apply {
        pairs.forEach { (key, value) -> this[key] = value }
    }
    loadKoinModules(savedHandleStateModule(handle))
}

fun dispatcherTestModule() = module {
    single<CoroutineDispatcher> { StandardTestDispatcher() }
    single<CoroutineScope> { TestScope(get<CoroutineDispatcher>()) }
}

fun authLocalDataSourceMockModule() = module {
    single<AuthLocalDataSource> {
        mock<AuthLocalDataSource>().apply {
            every { this@apply.getAccessToken() } returns flowOf("access_token")
            every { this@apply.getRefreshToken() } returns flowOf("refresh_token")
        }
    }
}

