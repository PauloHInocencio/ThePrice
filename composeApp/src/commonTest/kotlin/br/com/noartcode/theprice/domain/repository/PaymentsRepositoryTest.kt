package br.com.noartcode.theprice.domain.repository

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.ui.di.RobolectricTests
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentsRepositoryTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)

    private val database:ThePriceDatabase by inject()
    private val billsRepository:BillsRepository by inject()

    // Unit Under Test
    private val paymentsRepository:PaymentsRepository by inject()


    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
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
    fun `Should Retrieve and Insert Payments from the Server`() = runTest {

        // Fetching bills is necessary to avoid the `FOREIGN KEY constraint failed` error,
        // since payments have a many-to-one relationship with bills.
        billsRepository.fetchAllBills()

        paymentsRepository.getMonthPayments(month = 3, year = 2025).test {

            // Validating the initial state
            assertEquals(expected = 0, awaitItem().size)
            ensureAllEventsConsumed()

            // Fetching data from the API
            paymentsRepository.fetchAllPayments()

            // Validating final state
            assertEquals(expected = 3, awaitItem().size)
            ensureAllEventsConsumed()

        }
    }
}