package br.com.noartcode.theprice.data.local.datasource.bill

import app.cash.turbine.test
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.domain.model.Bill
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
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BillLocalDataSourceTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()

    private val database: ThePriceDatabase by inject()

    // Unit Under Test
    private val dataSource: BillLocalDataSource by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule()
            )
        }
        Dispatchers.setMain(testDispatcher)
    }


    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }


    @Test
    fun `Should successfully add new items into the database`() = runTest {

        repeat(3) { i ->
            val id = dataSource.insert(bill = stubBills[i])
            assertNotEquals(illegal = "", actual = id)
        }

        dataSource.getAllBills().test {
            assertEquals(3, awaitItem().size)
        }

    }

    @Test
    fun  `Should successfully return items by status`() = runTest {
        repeat(3) { i ->
            dataSource.insert(bill = stubBills[i])
        }

        dataSource.getBillsBy(Bill.Status.ACTIVE).test {
            assertEquals(3, awaitItem().size)
        }
    }

}