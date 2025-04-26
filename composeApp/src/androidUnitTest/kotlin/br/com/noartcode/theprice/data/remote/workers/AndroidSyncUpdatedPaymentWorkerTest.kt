package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.helpers.stubPayments
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.workers.factories.SyncUpdatedPaymentWorkerTestFactory
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
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
class AndroidSyncUpdatedPaymentWorkerTest : KoinTest {

    private val testScope = TestScope()
    private val coroutineDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)
    private val database: ThePriceDatabase by inject()
    private val paymentsRepository: PaymentsRepository by inject()
    private val billsRepository:BillsRepository by inject()
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(coroutineDispatcher)
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
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

        // Insert new payment
        val billID = billsRepository.insert(stubBills[0])
        val paymentID = paymentsRepository.insert(stubPayments[0].copy(billId = billID))

        // Check payment's initial sync state
        with(paymentsRepository.get(paymentID)){
            assertEquals(expected = false, this?.isSynced)
        }

        // Check worker result
        val worker = TestListenableWorkerBuilder<AndroidSyncUpdatedPaymentWorker>(context)
            .setWorkerFactory(SyncUpdatedPaymentWorkerTestFactory(paymentsRepository = paymentsRepository, ioDispatcher = coroutineDispatcher))
            .setInputData(workDataOf(SYNC_UPDATED_PAYMENT_INPUT_KEY to paymentID))
            .build()
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)

        // Check payment's final sync state
        with(paymentsRepository.get(paymentID)){
            assertEquals(expected = true, this?.isSynced)
        }
    }
}