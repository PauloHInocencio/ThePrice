package br.com.noartcode.theprice.domain.repository

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.ui.di.RobolectricTests
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BillsRepositoryTest : KoinTest, RobolectricTests() {

    private val coroutineDispatcher:CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()

    // Unit Under Test
    private val repository: BillsRepository by inject()


    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
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
    fun `Should Retrieve and Persist Bill From the Serve Side`() = runTest {

        repository.getAllBills().test {

            // Validating initial state
            assertEquals(expected = 0, awaitItem().size)
            ensureAllEventsConsumed()

            // Fetching data from the api
            repository.fetchAllBills()

            // Validating final state
            assertEquals(expected = 3, awaitItem().size)
            ensureAllEventsConsumed()
        }
    }

}