package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.util.Resource
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
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InsertBillTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val billDataSource: BillLocalDataSource by inject()

    // Unit Under Test
    private val insertBill: IInsertBill by inject()


    @BeforeTest
    fun before() {
        stopKoin()
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                commonTestModule(testDispatcher)
            )
        }
    }

    @AfterTest
    fun after(){
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }


    @Test
    fun `Add New Bill Should Succeed`() = runTest {
        val result = insertBill(
            Bill(
                name = "Internet",
                description = "My internet bill.",
                price = 12099,
                type = Bill.Type.MONTHLY,
                status = Bill.Status.ACTIVE,
                billingStartDate = DayMonthAndYear(day = 5, month = 9, year = 2024),
                createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
                updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            )
        )

        assertTrue(result is Resource.Success)
        assertNotEquals(illegal = "", actual = result.data)
    }

    @Test
    fun `Bill Retrieval Should Return Correct Details`() = runTest {
        val id = (insertBill(stubBills[0]) as Resource.Success).data

        with(billDataSource.getBill(id)) {
            assertEquals(expected = "internet", this?.name)
            assertEquals(expected = "My internet bill.", this?.description)
            assertEquals(expected = "05/09/2024", this?.billingStartDate.toString())
            assertEquals(expected = "19/02/2025", this?.createdAt?.toDayMonthAndYear().toString())
        }
    }
}