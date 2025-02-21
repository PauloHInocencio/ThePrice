package br.com.noartcode.theprice.data.localdatasource

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
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
class BillDataSourceTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database:ThePriceDatabase by inject()

    // Unit Under Test
    private val dataSource: BillLocalDataSource by inject()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        stopKoin() 
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                commonTestModule(testDispatcher),
                viewModelsModule()
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
    fun `Should successfully add new items into the database`() = runTest {

        repeat(3) { i ->
            val id = dataSource.insert(bill = stubBills[i])
            assertEquals(i + 1L, id)
        }

        dataSource.getAllBills().test {
            assertEquals(3, awaitItem().size)
        }

    }

    @Test
    fun `Should successfully return items by status`() = runTest {
        repeat(3) { i ->
            dataSource.insert(bill = stubBills[i])
        }

        dataSource.getBillsBy(Bill.Status.ACTIVE).test {
            assertEquals(3, awaitItem().size)
        }
    }

}