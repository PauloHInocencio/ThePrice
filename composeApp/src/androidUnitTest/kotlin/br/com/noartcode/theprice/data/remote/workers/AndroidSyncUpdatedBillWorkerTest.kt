package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.workers.factories.SyncUpdatedBillWorkerTestFactory
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AndroidSyncUpdatedBillWorkerTest : KoinTest {

    private val testScope = TestScope()
    private val coroutineDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)
    private val database: ThePriceDatabase by inject()
    private val repository: BillsRepository by inject()
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @BeforeTest
    fun before() {
        stopKoin()
        Dispatchers.setMain(coroutineDispatcher)
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                commonTestModule(coroutineDispatcher),
            )
        }
    }

    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `Should Persist Updated Bill Remotely`() = runTest {

        // Insert new bill
        val billID = repository.insert(stubBills[0])

        // Check bill's initial sync state
        with(repository.get(billID)){
            assertEquals(expected = false, this?.isSynced)
        }

        // Check worker result
        val worker = TestListenableWorkerBuilder<AndroidSyncUpdatedBillWorker>(context)
            .setWorkerFactory(SyncUpdatedBillWorkerTestFactory(billsRepository = repository, ioDispatcher = coroutineDispatcher))
            .setInputData(workDataOf(SYNC_UPDATED_BILL_INPUT_KEY to billID))
            .build()
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)

        // Check bill's final sync state
        with(repository.get(billID)){
            assertEquals(expected = true, this?.isSynced)
        }
    }
}