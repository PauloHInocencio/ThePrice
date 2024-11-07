package br.com.noartcode.theprice.data.localdatasource

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val database:ThePriceDatabase by inject()
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val dataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter) }

    @BeforeTest
    fun before() {
        startKoin { modules(platformTestModule(), commonTestModule()) }
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }


    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        database.close()
    }


    @Test
    fun `Should successfully add new items into the database`() = runTest {

        assertEquals(1, dataSource.insert(stubBills[0]))
        assertEquals(2, dataSource.insert(stubBills[1]))
        assertEquals(3, dataSource.insert(stubBills[2]))

        dataSource.getAllBills().test {
            assertEquals(3, awaitItem().size)
        }

    }

    @Test
    fun `Should successfully return items by status`() = runTest {

        dataSource.insert(stubBills[0])
        dataSource.insert(stubBills[1])
        dataSource.insert(stubBills[2])

        dataSource.getBillsBy(Bill.Status.ACTIVE).test {
            assertEquals(3, awaitItem().size)
        }


    }

}