package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class InsertMissingPaymentsTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val paymentDataSource: PaymentLocalDataSource by inject()
    private val billDataSource: BillLocalDataSource by inject()
    private val insertBill: IInsertBill by inject()

    // The Unit Under Test
    private val insertMissingPayments: IInsertMissingPayments by inject()


    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        startKoin{
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
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
    fun `Should Insert Missing Payments`() = runTest {
        val date = DayMonthAndYear(day = 21, month = 11, year = 2024)
        repeat(3) { i ->
            insertBill(
                bill = stubBills[i].copy(
                    billingStartDate = date
                )
            )
        }

        var payments = paymentDataSource
            .getMonthPayments(month = date.month, year = date.year)
            .first()
        assertEquals(expected = 0, actual = payments.size)

        insertMissingPayments(date)


        payments = paymentDataSource
            .getMonthPayments(month = date.month, year = date.year)
            .first()
        assertEquals(expected = 3, actual = payments.size)

    }

    @Test
    fun `Should insert missing payments only for bills in the correct date range`() = runTest {
        val date1 = DayMonthAndYear(day = 21, month = 1, year = 2024)
        val date2 = DayMonthAndYear(day = 21, month = 4, year = 2024)
        var index = 5
        while(index >= 0) {
            insertBill(
                bill = stubBills[index].copy(
                    billingStartDate = if (index >= 3) date1 else date2
                )
            )
            index--
        }

        // Check whether there are six bills in the database
        val bills = billDataSource.getAllBills().first()
        assertEquals(expected = 6, actual = bills.size)

        // Ensuring there are no payments in the database for the date1
        var payments = paymentDataSource
            .getMonthPayments(month = date1.month, year = date1.year)
            .first()
        assertEquals(expected = 0, actual = payments.size)

        // Should add payments only for bills that are in the date1 range
        insertMissingPayments(date1)

        // Ensuring there are only payments for the correct date range
        payments = paymentDataSource
            .getMonthPayments(month = date1.month, year = date1.year)
            .first()
        assertEquals(expected = 3, actual = payments.size)

    }

}