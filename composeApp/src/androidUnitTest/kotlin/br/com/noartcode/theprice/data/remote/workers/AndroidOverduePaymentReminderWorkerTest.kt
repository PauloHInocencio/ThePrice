package br.com.noartcode.theprice.data.remote.workers

import androidx.work.WorkInfo
import androidx.work.WorkManager
import app.cash.turbine.test
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.data.local.workers.OVERDUE_PAYMENT_REMINDER_WORKER_TAG
import br.com.noartcode.theprice.data.remote.workers.helpers.workManagerTestFactory
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.platform.notification.INotifier
import br.com.noartcode.theprice.ui.di.authLocalDataSourceMockModule
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
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
    manifest = Config.NONE,
    application = ThePriceAppTest::class
)
@OptIn(ExperimentalCoroutinesApi::class)
class AndroidOverduePaymentReminderWorkerTest : KoinTest {

    private val coroutineDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val workManager: WorkManager by inject()
    private val notifier: INotifier = mockk(relaxed = true)
    private val insertBillWithPayments: IInsertBillWithPayments by inject()
    private val getTodayDate: IGetTodayDate by inject()

    // Unit Under Test
    private val worker: IOverduePaymentReminderWorker by inject()


    @BeforeTest
    fun before() {
        startKoin {
            workManagerTestFactory()
            modules(
                module { single<INotifier> { notifier} },
                dispatcherTestModule(),
                commonModule(),
                commonTestModule(),
                platformTestModule(),
                authLocalDataSourceMockModule()
            )
        }
        Dispatchers.setMain(coroutineDispatcher)
    }

    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `Worker Should Notify Overdue Payments`() = runTest {
        // GIVEN
        // Set Current Day to November 15th
        val currentDate = DayMonthAndYear(day = 2, month = 11, year = 2025)
        (getTodayDate as GetTodayDateStub).date = currentDate

        // Populating the database with six different bills — All in the same month but in different days
        repeat(6) { i ->
            val dueDay = 1
            insertBillWithPayments(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = dueDay + i, month = 11, year = 2025)
                ),
                currentDate = currentDate
            )
        }

        // WHEN
        worker.sweepDailyDue()

        // THEN
        workManager.getWorkInfosByTagFlow(OVERDUE_PAYMENT_REMINDER_WORKER_TAG).test {

            skipItems(1)
            val workInfo = awaitItem().last()
            assertEquals(expected = WorkInfo.State.ENQUEUED, actual = workInfo.state)

            // Only four payments need to be notify
            verify(exactly = 4) { notifier.showOverduePaymentNotification(title = any(), description = any(), paymentId = any())  }
        }
    }

}