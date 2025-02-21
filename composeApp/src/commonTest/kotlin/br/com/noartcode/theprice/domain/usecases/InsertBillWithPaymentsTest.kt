package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.util.Resource
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InsertBillWithPaymentsTest: KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(scheduler = testScope.testScheduler)

    private val database:ThePriceDatabase by inject()
    private val billDataSource:BillLocalDataSource by inject()
    private val paymentDataSource:PaymentLocalDataSource by inject()

    // Unit Under Test
    private val insertBillWithPayments:IInsertBillWithPayments by inject()

    @BeforeTest
    fun before() {
        stopKoin()
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(commonModule())
            modules(commonTestModule(testDispatcher))
            modules(platformTestModule())
        }
    }


    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }


    @Test
    fun `Insert Bill With Payments Should Succeed`() = runTest {
        val result = insertBillWithPayments(
            bill = stubBills[0],
            currentDate = DayMonthAndYear(day = 21, month = 2, year = 2025)
        )

        assertTrue(result is Resource.Success)
        assertEquals(expected = 1, result.data)
    }

    @Test
    fun `Bill Retrieval Should Return Correct Details`() = runTest {
        val result = insertBillWithPayments(
            bill = stubBills[0],
            currentDate =  DayMonthAndYear(day = 21, month = 2, year = 2025)
        )

        val id = (result as Resource.Success).data

        with(billDataSource.getBill(id)) {
            assertEquals(expected = "internet", this?.name)
            assertEquals(expected = "My internet bill.", this?.description)
            assertEquals(expected = "05/09/2024", this?.billingStartDate.toString())
            assertEquals(expected = "19/02/2025", this?.createdAt?.toDayMonthAndYear().toString())
        }
    }

    @Test
    fun `Payments Retrieval By Bill ID Should Return Correct Amount Of Data`() = runTest {
        val result = insertBillWithPayments(
            bill = stubBills[0],
            currentDate = DayMonthAndYear(day = 21, month = 2, year = 2025)
        )

        val id = (result as Resource.Success).data

        val payments = paymentDataSource.getBillPayments(id)

        assertEquals(expected = 6, payments.size)
    }
}