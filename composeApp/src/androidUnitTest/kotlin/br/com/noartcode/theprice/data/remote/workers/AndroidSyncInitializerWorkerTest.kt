package br.com.noartcode.theprice.data.remote.workers

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import br.com.noartcode.theprice.ThePriceAppTest
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.remote.workers.factories.SyncInitializerWorkerTestFactory
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
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
class AndroidSyncInitializerWorkerTest : KoinTest {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)
    private val database:ThePriceDatabase by inject()
    private val billsRepository:BillsRepository by inject()
    private val paymentsRepository:PaymentsRepository by inject()
    private val context = ApplicationProvider.getApplicationContext<Context>()


    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
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
    fun `Should Sync Bills And Payments From Cloud To Local Storage`() = runTest {

        // Making sure that there's no local data
        assertEquals(expected = 0, actual = billsRepository.getAllBills().first().size)
        assertEquals(expected = 0, actual = paymentsRepository.getAllPayments().first().size)


        // Check worker result
        val worker = TestListenableWorkerBuilder<AndroidSyncInitializerWorker>(context)
            .setWorkerFactory(
                SyncInitializerWorkerTestFactory(
                    billsRepository = billsRepository,
                    paymentsRepository = paymentsRepository,
                    ioDispatcher = testDispatcher
                )
            )
            .build()
        val result = worker.doWork()

        assertTrue(result is ListenableWorker.Result.Success)

        // Check whether all data was successfully persisted
        assertEquals(expected = 3, actual = billsRepository.getAllBills().first().size)
        assertEquals(expected = 9, actual = paymentsRepository.getAllPayments().first().size)
    }


}