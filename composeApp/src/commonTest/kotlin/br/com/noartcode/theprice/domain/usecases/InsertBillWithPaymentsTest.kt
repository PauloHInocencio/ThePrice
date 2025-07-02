package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.util.Resource
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InsertBillWithPaymentsTest: KoinTest, RobolectricTests() {

    private val testDispatcher:CoroutineDispatcher by inject()
    private val database:ThePriceDatabase by inject()
    private val billDataSource:BillLocalDataSource by inject()
    private val paymentDataSource:PaymentLocalDataSource by inject()

    // Unit Under Test
    private val insertBillWithPayments: IInsertBillWithPayments by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
                platformTestModule()
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
    fun `Insert Bill With Payments Should Succeed`() = runTest {
        val result = insertBillWithPayments(
            bill = stubBills[0],
            currentDate = DayMonthAndYear(day = 21, month = 2, year = 2025)
        )

        assertTrue(result is Resource.Success)

        val (bill, _) = result.data
        assertNotEquals(illegal = "", actual = bill.id)
    }

    @Test
    fun `Bill Retrieval Should Return Correct Details`() = runTest {
        val result = insertBillWithPayments(
            bill = stubBills[0],
            currentDate =  DayMonthAndYear(day = 21, month = 2, year = 2025)
        )

        val (bill, _) = (result as Resource.Success).data

        with(billDataSource.getBill(bill.id)) {
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

        val (_, payments) = (result as Resource.Success).data

        assertEquals(expected = 6, payments.size)
    }
}