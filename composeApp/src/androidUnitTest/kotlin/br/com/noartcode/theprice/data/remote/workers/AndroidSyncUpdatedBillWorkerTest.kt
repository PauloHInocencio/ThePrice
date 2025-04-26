package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.TestDriver
import androidx.work.testing.WorkManagerTestInitHelper
import app.cash.turbine.test
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock
import br.com.noartcode.theprice.data.remote.workers.helpers.workManagerTestFactory
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


@RunWith(RobolectricTestRunner::class)
@Config(
    manifest= Config.NONE,
    application = ThePriceAppTest::class
)
@OptIn(ExperimentalCoroutinesApi::class)
class AndroidSyncUpdatedBillWorkerTest : KoinTest {
    private val coroutineDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val repository: BillsRepository by inject()
    private val workManager: WorkManager by inject()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private var testDriver: TestDriver? = null

    // Unit Under Test
    private val syncUpdatedBillWorker:ISyncUpdatedBillWorker by inject()

    @BeforeTest
    fun before() {
        startKoin {
            workManagerTestFactory()
            modules(
                dispatcherTestModule(),
                commonModule(),
                commonTestModule(),
                platformTestModule(),
            )
        }
        testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        Dispatchers.setMain(coroutineDispatcher)
    }

    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `Should Persist Updated Bill Remotely`() = runTest {

        // Given
        val billID = repository.insert(stubBills[0])

        // When
        syncUpdatedBillWorker.sync(billID)

        // Then
        workManager.getWorkInfosForUniqueWorkFlow(billID).test {

            // met all constraints
            var workInfo = awaitItem().last()
            testDriver?.setAllConstraintsMet(workInfo.id)


            // final state should be succeeded
            skipItems(1)
            workInfo = awaitItem().last()
            assertEquals(expected = WorkInfo.State.SUCCEEDED, actual = workInfo.state)
        }
    }

    @Test
    fun `Should Change Bill isSynced property to true`() = runTest {

        // Given
        val billID = repository.insert(stubBills[0])

        // When
        syncUpdatedBillWorker.sync(billID)

        // Then
        workManager.getWorkInfosForUniqueWorkFlow(billID).test {

            // met all constraints
            val workInfo = awaitItem().last()
            testDriver?.setAllConstraintsMet(workInfo.id)

            skipItems(2)
            ensureAllEventsConsumed()

            assertEquals(expected = true, repository.get(billID)?.isSynced)
        }
    }

    @Test
    fun `Should Receive An Error When Trying to Sync Bill And Retry`() = runTest {

        // Given
        val billID = repository.insert(stubBills[0].copy(ThePriceApiMock.BILL_FAILED_ID))

        // When
        syncUpdatedBillWorker.sync(billID)

        // Then
        workManager.getWorkInfosForUniqueWorkFlow(billID).test {

            // met all constraints
            var workInfo = awaitItem().last()
            testDriver?.setAllConstraintsMet(workInfo.id)

            skipItems(1)
            workInfo = awaitItem().last()

            assertEquals(expected = WorkInfo.State.ENQUEUED, actual = workInfo.state)
            cancelAndConsumeRemainingEvents()
        }
    }
}