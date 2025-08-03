package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBill
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.domain.usecases.payment.IGetPayments
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
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
class GetPaymentsTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val paymentDataSource: PaymentLocalDataSource by inject()
    private val insertBill: IInsertBill by inject()
    private val insertBillWithPayments: IInsertBillWithPayments by inject()

    // The Unit Under Test
    private val getPayments: IGetPayments by inject()


    @BeforeTest
    fun before() {
        startKoin{
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
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
    fun `Try To Get Payments of a Invalid Month should return an Empty list`() = runTest {
        val numOfPayments = 3
        val result = insertBillWithPayments(
            bill = stubBills[0].copy(
                billingStartDate = DayMonthAndYear(day = 5, month = 10, year = 2024)
            ),
            currentDate = DayMonthAndYear(day = 5, month = 12, year = 2024),
        )
        val (bill, _) = (result as Resource.Success).data

        // check if the current bill has only the correct amount of payments
        val payments =  paymentDataSource.getBillPayments(bill.id)
        assertEquals(
            expected = numOfPayments,
            actual = payments.size
        )


        getPayments(
            date = DayMonthAndYear(day = 5, month = 9, year = 2024),
            billStatus = Bill.Status.ACTIVE
        ).test {

            with(awaitItem()) {
                assertEquals(expected = true, actual = (this is Resource.Loading))
            }

            with(awaitItem()) {
                assertEquals(
                    expected = 0,
                    actual = (this as Resource.Success).data.size
                )
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Try To Get a Payments in a Valid Month With a Empty DB Should Create and Return Payments`() = runTest {
        val numOfValidMonths = 3
        val initialMonth = 10
        val result = insertBillWithPayments(
            bill = stubBills[0].copy(
                billingStartDate = DayMonthAndYear(day = 5, month = initialMonth, year = 2024)
            ),
            currentDate = DayMonthAndYear(day = 5, month = 12, year = 2024),
        )
        val (bill, _) = (result as Resource.Success).data

        // check if the current bill has no payments
        var payments =  paymentDataSource.getBillPayments(bill.id)
        assertEquals(
            expected = 3,
            actual = payments.size
        )

        // Calling getPayments numOfValidMonths times
        repeat(numOfValidMonths) { i ->
            getPayments(
                date = DayMonthAndYear(day = 5, month = initialMonth + i, year = 2024),
                billStatus = Bill.Status.ACTIVE
            ).first()
        }


        payments = paymentDataSource.getBillPayments(bill.id)
        assertEquals(
            expected = numOfValidMonths,
            actual = payments.size
        )
    }


    @Test
    fun `When individual payment is updated Get Payment should be re executed`() = runTest{

        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            insertBillWithPayments(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024)
                ),
                currentDate = DayMonthAndYear(day = 5, month = 1, year = 2025),
            )
        }

        getPayments(DayMonthAndYear(day = 21, month = 11, year = 2024), billStatus = Bill.Status.ACTIVE).test {


            with(awaitItem()) {
                assertEquals(expected = true, actual = this is Resource.Loading)
            }

            val result = (awaitItem() as Resource.Success)
            with(result) {
                assertEquals(expected = 3, actual = data.size)
            }


            val originalPayment = result.data.first()

            // Updates one of the payments
            paymentDataSource.update(
                originalPayment.copy(
                    price = 1111,
                    isPayed = true
                )
            )

            with(awaitItem() as Resource.Success) {
                assertEquals(expected = 3, actual = data.size)
                assertEquals(expected = 1111, data.find { it.id == originalPayment.id }?.price)
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Should Return The Correct List of Payments for All Valid Months` () = runTest {

        insertBillWithPayments(
            bill = stubBills[0].copy(
                billingStartDate = DayMonthAndYear(day = 5, month = 8, year = 2024)
            ),
            currentDate = DayMonthAndYear(day = 5, month = 1, year = 2025),
        )


        val currentDate = MutableStateFlow(DayMonthAndYear(day = 5, month = 12, year = 2024))

        currentDate
            .flatMapLatest { date ->
                getPayments(date, billStatus = Bill.Status.ACTIVE)
            }.test {

                //For all the 5 months
                repeat(5) {

                    //Skip the loading state
                    skipItems(1)

                    //Check the list of payments
                    with(awaitItem()) {
                        val payment = (this as Resource.Success).data.first()
                        assertEquals(expected = currentDate.value.toString() , actual = payment.dueDate.toString())
                        assertEquals(expected = 1, actual = data.size)
                    }

                    ensureAllEventsConsumed()

                    //Going back to valid previous month
                    currentDate.update { it.copy(month = it.month - 1) }
                }


                //Going back to an invalid previous month
                currentDate.update { it.copy(month = it.month - 1) }

                // Should return a empty list
                skipItems(1)
                with(awaitItem()) {
                    assertEquals(expected = 0, actual = (this as Resource.Success).data.size)
                }

                ensureAllEventsConsumed()
            }
    }

    @Test
    fun `Payment With Due Day Later this Month should be shown`() = runTest {
        val currentDate = DayMonthAndYear(day = 5, month = 8, year = 2025)

        insertBillWithPayments(
            bill = stubBills[0].copy(
                billingStartDate = DayMonthAndYear(day = 10, month = 8, year = 2025)
            ),
            currentDate = currentDate,
        )

        getPayments(currentDate, billStatus = Bill.Status.ACTIVE).test {

            // First emission should be a Loading state
            with(awaitItem()) {
                assertEquals(expected = true, actual = (this is Resource.Loading))
            }

            // Check the list of payments
            with(awaitItem()) {
                assertEquals(
                    expected = 1,
                    actual = (this as Resource.Success).data.size
                )
            }

            ensureAllEventsConsumed()

        }
    }
}


