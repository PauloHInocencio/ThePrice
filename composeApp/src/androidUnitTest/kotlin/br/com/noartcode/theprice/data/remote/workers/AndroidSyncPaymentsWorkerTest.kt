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
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.IInsertBill
import br.com.noartcode.theprice.domain.usecases.IInsertMissingPayments
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class AndroidSyncPaymentsWorkerTest : KoinTest {

    private val coroutineDispatcher:CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val paymentsRepository: PaymentsRepository by inject()
    private val insertBill: IInsertBill by inject()
    private val insertMissingPayments: IInsertMissingPayments by inject()
    private val workManager: WorkManager by inject()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private var testDriver: TestDriver? = null

    // Unit Under Test
    private val syncPaymentsWorker: ISyncPaymentsWorker by inject()

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
    fun `Should Insert Payments locally and sync them remotely`() = runTest {

        // Given
        val date = DayMonthAndYear(day = 21, month = 11, year = 2024)
        repeat(3) { i ->
            insertBill(bill = stubBills[i].copy(billingStartDate = date))
        }
        insertMissingPayments(date)

        // When
        syncPaymentsWorker.sync()

        // Then
        workManager.getWorkInfosForUniqueWorkFlow(SYNC_PAYMENTS_WORKER_NAME).test {

            // met all constraints
            var workInfo = awaitItem().last()
            testDriver?.setAllConstraintsMet(workInfo.id)

            skipItems(1)
            workInfo = awaitItem().last()

            assertEquals(expected = WorkInfo.State.SUCCEEDED, actual = workInfo.state)
            ensureAllEventsConsumed()
        }


    }


    @Test
    fun `Should Change Payments isSynced property to true`() = runTest {

        // Given
        val date = DayMonthAndYear(day = 21, month = 11, year = 2024)
        repeat(3) { i ->
            insertBill(bill = stubBills[i].copy(billingStartDate = date))
        }
        insertMissingPayments(date)

        // When
        syncPaymentsWorker.sync()

        // Then
        workManager.getWorkInfosForUniqueWorkFlow(SYNC_PAYMENTS_WORKER_NAME).test {

            // met all constraints
            val workInfo = awaitItem().last()
            testDriver?.setAllConstraintsMet(workInfo.id)

            skipItems(2)

            val numSyncPayments = paymentsRepository
                .getMonthPayments(date.month, date.year)
                .first()
                .filter { it.isSynced }
                .size

            assertEquals(expected = 3, actual = numSyncPayments)
        }

    }


    @Test
    fun `Should Receive An Error When Trying to Sync Payments And Retry`() = runTest {

        // Given
        val date = DayMonthAndYear(day = 21, month = 11, year = 2024)
        repeat(3) { i ->
            insertBill(bill = stubBills[i].copy(billingStartDate = date, id = ThePriceApiMock.BILL_FAILED_ID))
        }
        insertMissingPayments(date)

        // When
        syncPaymentsWorker.sync()

        // Then
        workManager.getWorkInfosForUniqueWorkFlow(SYNC_PAYMENTS_WORKER_NAME).test {

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