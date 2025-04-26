package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.workers.factories.SyncPaymentsWorkerTestFactory
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.IInsertBill
import br.com.noartcode.theprice.domain.usecases.IInsertMissingPayments
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest= Config.NONE,
    application = ThePriceAppTest::class
)
@OptIn(ExperimentalCoroutinesApi::class)
class AndroidSyncPaymentsWorkerTest : KoinTest {

    private val testScope = TestScope()
    private val coroutineDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)
    private val database: ThePriceDatabase by inject()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val paymentsRepository: PaymentsRepository by inject()
    private val insertBill: IInsertBill by inject()
    private val insertMissingPayments: IInsertMissingPayments by inject()


    @BeforeTest
    fun before() {
        Dispatchers.setMain(coroutineDispatcher)
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule()
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
    fun `Should Insert Payments locally and sync them remotely`() = runTest {

        // Create bills
        val date = DayMonthAndYear(day = 21, month = 11, year = 2024)
        repeat(3) { i ->
            insertBill(
                bill = stubBills[i].copy(
                    billingStartDate = date
                )
            )
        }

        // Create payments for the current month
        insertMissingPayments(date)

        // Check num of no sync payments
        var numNotSyncPayments = paymentsRepository
            .getMonthPayments(date.month, date.year)
            .first()
            .filter { it.isSynced.not() }
            .size

        assertEquals(expected = 3, actual = numNotSyncPayments)

        // Check worker result
        val worker = TestListenableWorkerBuilder<AndroidSyncPaymentsWorker>(context)
            .setWorkerFactory(SyncPaymentsWorkerTestFactory(paymentsRepository = paymentsRepository, ioDispatcher = coroutineDispatcher))
            .build()
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)

        // Check num of no sync payments
        numNotSyncPayments = paymentsRepository
            .getMonthPayments(date.month, date.year)
            .first()
            .filter { it.isSynced.not() }
            .size

        assertEquals(expected = 0, actual = numNotSyncPayments)
    }

}