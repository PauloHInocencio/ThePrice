package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.populateDBWithAnBillAndFewPayments
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.util.Resource
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
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class GetPaymentsTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val paymentDataSource: PaymentLocalDataSource by lazy { PaymentLocalDataSourceImp(database) }
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val getTodayDate: IGetTodayDate = GetTodayDateStub()
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter, getTodayDate) }

    // The Unit Under Test
    private val getPayments: IGetPayments by lazy {
        GetPayments(
            billLDS = billDataSource,
            paymentLDS = paymentDataSource,
            dispatcher = testDispatcher
        )
    }


    @BeforeTest
    fun before() {
        startKoin { modules(platformTestModule(), commonTestModule()) }
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        database.close()
    }


    @Test
    fun `Try To Get Payments of a Invalid Month should return an Empty list`() = runTest {
        val numOfPayments = 3
        val billId = populateDBWithAnBillAndFewPayments(
            bill = stubBills[0],
            billingStartDate = DayMonthAndYear(day = 5, month = 10, year = 2024),
            numOfPayments = numOfPayments,
            billDataSource,
            paymentDataSource
        )

        // check if the current bill has only the correct amount of payments
        val payments =  paymentDataSource.getBillPayments(billId)
        assertEquals(
            expected = numOfPayments,
            actual = payments.size
        )

        val result = getPayments(
            date = DayMonthAndYear(day = 5, month = 9, year = 2024),
            billStatus = Bill.Status.ACTIVE
        ).first()


        assertEquals(
            expected = emptyList(),
            actual = (result as Resource.Success<List<Payment>>).data
        )
    }

    @Test
    fun `Try To Get a Payments in a Valid Month With a Empty DB Should Create and Return Payments`() = runTest {
        val numOfValidMonths = 3
        val initialMonth = 10
        val billId = populateDBWithAnBillAndFewPayments(
            bill = stubBills[0],
            billingStartDate = DayMonthAndYear(day = 5, month = initialMonth, year = 2024),
            numOfPayments = 0,
            billDataSource,
            paymentDataSource
        )

        // check if the current bill has no payments
        var payments =  paymentDataSource.getBillPayments(billId)
        assertEquals(
            expected = 0,
            actual = payments.size
        )

        // Calling getPayments numOfValidMonths times
        repeat(numOfValidMonths) { i ->
            getPayments(
                date = DayMonthAndYear(day = 5, month = initialMonth + i, year = 2024),
                billStatus = Bill.Status.ACTIVE
            ).first()
        }


        payments = paymentDataSource.getBillPayments(billId)
        assertEquals(
            expected = numOfValidMonths,
            actual = payments.size
        )
    }


    @Test
    fun `Try To Get a Payments in a Invalid Day should return an Empty list`() = runTest {

        val billId = populateDBWithAnBillAndFewPayments(
            bill = stubBills[0],
            billingStartDate = DayMonthAndYear(day = 5, month = 10, year = 2024),
            numOfPayments = 0,
            billDataSource,
            paymentDataSource
        )

        // check if the current bill has no payments
        val payments =  paymentDataSource.getBillPayments(billId)
        assertEquals(
            expected = 0,
            actual = payments.size
        )

        val result = getPayments(
            date = DayMonthAndYear(day = 3, month = 10, year = 2024),
            billStatus = Bill.Status.ACTIVE
        ).first()


        assertEquals(
            expected = emptyList(),
            actual = (result as Resource.Success<List<Payment>>).data
        )
    }



    @Test
    fun `When individual payment is updated Get Payment should be re executed`() = runTest{

        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            populateDBWithAnBillAndFewPayments(
                bill = stubBills[i],
                billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024),
                numOfPayments = 0,
                billDataSource,
                paymentDataSource
            )
        }

        getPayments(DayMonthAndYear(day = 21, month = 11, year = 2024), billStatus = Bill.Status.ACTIVE).test(timeout =  60.seconds) {


            val result = awaitItem()
            assertEquals(expected = true, actual = result is Resource.Success)
            assertEquals(expected = 3, actual = (result as Resource.Success).data.size)


            // Updates one of the payments
            paymentDataSource.updatePayment(
                id = 1,
                paidValue = 1111,
                paidAt = DayMonthAndYear(day = 21, month = 11, year = 2024)
            )

            with(awaitItem() as Resource.Success) {
                assertEquals(expected = 3, actual = data.size)
                assertEquals(expected = 1111, data.find { it.id == 1L }?.payedValue)
            }

            ensureAllEventsConsumed()
        }
    }

}


