package br.com.noartcode.theprice.data.localdatasource

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
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
    private val getTodayDate: IGetTodayDate = GetTodayDateStub()
    private val dataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter, getTodayDate) }

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

        repeat(3) { i ->
            val bill = stubBills[i]
            val id = dataSource.insert(
                name = bill.name,
                description = bill.description,
                price = bill.price,
                type = bill.type,
                status = bill.status,
                billingStartDate = bill.billingStartDate,
            )
            assertEquals(i + 1L, id)
        }

        dataSource.getAllBills().test {
            assertEquals(3, awaitItem().size)
        }

    }

    @Test
    fun `Should successfully return items by status`() = runTest {
        repeat(3) { i ->
            val bill = stubBills[i]
            dataSource.insert(
                name = bill.name,
                description = bill.description,
                price = bill.price,
                type = bill.type,
                status = bill.status,
                billingStartDate = bill.billingStartDate,
            )
        }

        dataSource.getBillsBy(Bill.Status.ACTIVE).test {
            assertEquals(3, awaitItem().size)
        }


    }

}