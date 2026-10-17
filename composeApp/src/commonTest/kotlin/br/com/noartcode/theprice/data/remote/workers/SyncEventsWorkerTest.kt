package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.mapper.toSyncEvent
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.authLocalDataSourceMockModule
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.syncEventsUnauthorizedModule
import dev.mokkery.matcher.any
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SyncEventsWorkerTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val billRemoteDataSource: BillRemoteDataSource by inject()

    // Unit Under Test
    private val worker: ISyncEventsWorker by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
                authLocalDataSourceMockModule(),
                syncEventsUnauthorizedModule(event = stubBills[0].toSyncEvent("delete")),
            )
        }
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun after() {
        worker.stop()
        Dispatchers.resetMain()
        stopKoin()
        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }

    @Test
    fun `When sync is unauthorized it should stop instead of retrying`() = runTest {
        // GIVEN: a queued event whose sync is rejected with Unauthorized (syncEventsUnauthorizedModule)

        // WHEN
        worker.start()
        advanceTimeBy(20_000.milliseconds) // a retry loop would call the server again every 5s

        // THEN
        verifySuspend(VerifyMode.exactly(1)) { billRemoteDataSource.delete(any()) }
    }
}
