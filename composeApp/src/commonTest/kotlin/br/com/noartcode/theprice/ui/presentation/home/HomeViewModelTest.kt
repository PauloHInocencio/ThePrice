package br.com.noartcode.theprice.ui.presentation.home

import app.cash.turbine.test
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBill
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class HomeViewModelTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val paymentDataSource: PaymentLocalDataSource by inject()
    private val getTodayDate: IGetTodayDate by inject()
    private val insertBill: IInsertBill by inject()
    private val insertBillWithPayments: IInsertBillWithPayments by inject()
    private val authLocalDataSource: AuthLocalDataSource by inject()


    // Unit Under Test
    private val viewModel:HomeViewModel by inject()

    @BeforeTest
    fun before() {
        startKoin{
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
                viewModelsModule()
            )
        }
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()

        // Clean up file that was created for shared preferences
        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }


    @Test
    fun `Should emit correct sequence of state on the ViewModel initialization`() = runTest {


        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            insertBillWithPayments(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024)
                ),
                currentDate = DayMonthAndYear(day = 21, month = 11, year = 2024),
            )
        }


        // Set Current Day to November 21th
        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test{

            // check initial state
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 0, actual = totalPayments)
                assertEquals(expected = 0, actual = paymentSection.size)
                assertEquals(expected = "", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }

            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }


            // Final State
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 3, actual = totalPayments)
                assertEquals(expected = 1, actual = paymentSection.size)
                assertEquals(expected = "12 Novembro", actual = paymentSection[0].title)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }


            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Allows Navigation To Next Month From Current Month`() = runTest {
        val currentDate = DayMonthAndYear(day = 4, month = 8, year = 2025)
        insertBillWithPayments(
            bill = stubBills[0].copy(
                billingStartDate = DayMonthAndYear(day = 4, month = 7, year = 2025)
            ),
            currentDate = currentDate,
        )

        // Set Current Day to August 4th
        (getTodayDate as GetTodayDateStub).date =  currentDate


        viewModel.uiState.test{

            // skip the initial-empty and loading states
            skipItems(2)

            // Check whether the user can navigate to the next month
            with(awaitItem()) {
                assertEquals(expected = true, actual = canGoNext)
            }

            // Navigate to the next month
            viewModel.onEvent(HomeEvent.OnGoToNexMonth)

            // Skip loading state
            skipItems(1)

            // Check final state
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 1, actual = totalPayments)
                assertEquals(expected = "4 Setembro", actual = paymentSection[0].title)
                assertEquals(expected = "Setembro - 2025", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }
        }
    }

    @Test
    fun `When No Payments Found Navigation Should Not Be Allowed`() = runTest {
        // Set Current Day to August 5th
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 5, month = 8, year = 2025)

        viewModel.uiState.test {

            // skip the initial-empty and loading states
            skipItems(2)

            // Check final state
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { paymentSection.size }
                assertEquals(expected = 0, actual = totalPayments)
                assertEquals(expected = "Agosto - 2025", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }
        }
    }

    @Test
    fun `Should emit correct sequence of states when User Went back to Previous Month`() = runTest {


        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            insertBillWithPayments(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024)
                ),
                currentDate = DayMonthAndYear(day = 21, month = 11, year = 2024),
            )
        }


        // Set Current Day to November 21th
        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test{

            // Ignoring empty state
            skipItems(1)

            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Final State
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 3, actual = totalPayments)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }

            ensureAllEventsConsumed()

            // Going back to previous Month
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)


            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Final State
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 2, actual = totalPayments)
                assertEquals(expected = "Outubro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }

            ensureAllEventsConsumed()

            // Going back to previous Month
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)

            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Final State
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { paymentSection.size }
                assertEquals(expected = 1, actual = totalPayments)
                assertEquals(expected = "Setembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }

            ensureAllEventsConsumed()

        }
    }

    @Test
    fun `Should emit correct sequence of states when User Update Payment Status`() = runTest {

        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            insertBillWithPayments(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024)
                ),
                currentDate = DayMonthAndYear(day = 21, month = 11, year = 2024),
            )
        }

        // Set Current Day to November 21th
        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test {

            // Ignoring states
            skipItems(2)

            // Final State
            val result = awaitItem()
            with(result) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 3, actual = totalPayments)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }

            ensureAllEventsConsumed()

            // Check payment info
            val originalPayment = result
                .paymentSection
                .flatMap { it.paymentUis }
                .find { it.billName == "mom's internet" }!!

            assertEquals(expected = PaymentUi.Status.OVERDUE, originalPayment.status)

            // Simulate user changing payment status
            viewModel.onEvent(
                HomeEvent.OnPaymentStatusClicked(
                id = originalPayment.id,
                status = originalPayment.status,
            ))

            // Check final state
            val newResult = awaitItem()
            with(newResult) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 3, actual = totalPayments)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }

            ensureAllEventsConsumed()

            // Confirm that selected payment has changed
            val updatedPayment = newResult.paymentSection
                .flatMap { it.paymentUis }
                .find { it.billName == "mom's internet" }!!

            assertEquals(expected = originalPayment.id, actual = updatedPayment.id)
            assertEquals(expected = PaymentUi.Status.PAYED, updatedPayment.status)

        }

    }

    @Test
    fun `Payment Amount and Due Date Should Not Change when User Update Payment Status`() = runTest {

        // Populating the database with 1 bill
        val result = insertBillWithPayments(
            bill = stubBills[1].copy(
                billingStartDate = DayMonthAndYear(day = 12, month = 9, year = 2024)
            ),
            currentDate = DayMonthAndYear(day = 21, month = 11, year = 2024),
        )

        assertTrue(result is Resource.Success)

        // Set Current Day to November 21th
        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 21, month = 11, year = 2024)

        viewModel.uiState.test {

            // Ignoring states
            skipItems(2)

            val originalPayment = awaitItem()
                .paymentSection
                .flatMap { it.paymentUis }
                .find { it.billName == "mom's internet" }!!

            ensureAllEventsConsumed()

            with(originalPayment) {
                assertEquals(expected = PaymentUi.Status.OVERDUE, actual =  status)
                assertEquals(expected = "R$ 99,99", actual = price)
                assertEquals(expected = "9 days overdue", actual = statusDescription)
            }

            // Simulate user changing payment status
            viewModel.onEvent(
                HomeEvent.OnPaymentStatusClicked(
                id = originalPayment.id,
                status = originalPayment.status,
            ))


            // Confirm the new status
            with(awaitItem()) {
                val payment = paymentSection
                    .flatMap { it.paymentUis }
                    .find { it.billName == "mom's internet" }
                assertEquals(PaymentUi.Status.PAYED, payment?.status)
            }

            ensureAllEventsConsumed()

            // Simulate user changing payment info
            val (bill, _) = result.data
            paymentDataSource.update(
                Payment(
                    id = originalPayment.id,
                    billId = bill.id,
                    price = 10000,
                    dueDate = DayMonthAndYear(day = 5, month = 11, year = 2024),
                    isPayed = false,
                    createdAt = 0L,
                    updatedAt = 0L,
                    isSynced = false,
                )

            )


            // Get the update version of the payment
            val updatedPayment = awaitItem()
                .paymentSection
                .flatMap { it.paymentUis }
                .find { it.billName == "mom's internet" }!!


            ensureAllEventsConsumed()

            with(updatedPayment) {
                assertEquals(expected = PaymentUi.Status.OVERDUE, status)
                assertEquals(expected = "R$ 100,00", actual = price)
                assertEquals(expected = "9 days overdue", actual = statusDescription)
            }
        }
    }

    @Test
    fun `Should Return The Correct List of Payments for All Valid Months`() = runTest {

        insertBillWithPayments(
            bill = stubBills[0].copy(
                billingStartDate = DayMonthAndYear(day = 5, month = 8, year = 2024)
            ),
            currentDate = DayMonthAndYear(day = 5, month = 12, year = 2024),
        )

        // Set Current Day to November 21th
        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 5, month = 12, year = 2024)

        val monthNames = listOf("Novembro - 2024", "Outubro - 2024", "Setembro - 2024", "Agosto - 2024")
        viewModel.uiState.test {

            skipItems(2)
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 1, actual = totalPayments)
                assertEquals(expected = "Dezembro - 2024", actual = monthName)

            }

            repeat(4) { i ->

                viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)

                skipItems(1)
                with(awaitItem()) {
                    val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                    assertEquals(expected = 1, actual = totalPayments)
                    assertEquals(expected = monthNames[i], actual = monthName)


                    when(i) {
                        // The user should be able to go back From September to August
                        2 -> assertEquals(expected = true, canGoBack)
                        // The user shouldn't be able to go back Form August to July
                        3 -> assertEquals(expected = false, canGoBack)
                        // Ignoring other emissions
                        else -> Unit
                    }

                }
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Valid Payments Should Be Inserted and Returned If Not Exists`() = runTest {

        repeat(3) { i ->
            insertBill(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = 21, month = 11, year = 2024)
                )
            )
        }

        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test{

            // check initial state
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 0, actual = totalPayments)
                assertEquals(expected = "", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }

            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Final State
            with(awaitItem()) {
                val totalPayments = paymentSection.sumOf { it.paymentUis.size }
                assertEquals(expected = 3, actual = totalPayments)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected = true, actual = canGoNext)
            }


            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Should Group Payments Into Correct Sections By Due Date`() = runTest {
        val startDays = listOf(5, 12, 12, 25) // Will create sections for days 5, 12, and 25
        repeat(4) { i ->
            insertBillWithPayments(
                bill = stubBills[i].copy(
                    billingStartDate = DayMonthAndYear(day = startDays[i], month = 11, year = 2024)
                ),
                currentDate = DayMonthAndYear(day = 21, month = 11, year = 2024),
            )
        }

        // Set Current Day to November 21st
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 21, month = 11, year = 2024)

        viewModel.uiState.test {

            // Skip initial empty and loading states
            skipItems(2)

            // Final State
            with(awaitItem().paymentSection) {
                val totalPayments = this.sumOf { it.paymentUis.size }
                assertEquals(expected = 4, actual = totalPayments)
                assertEquals(expected = 3, actual = this.size)

                assertEquals(expected = "5 Novembro", actual = this[0].title)
                assertEquals(expected = 1, actual = this[0].paymentUis.size)

                assertEquals(expected = "12 Novembro", actual = this[1].title)
                assertEquals(expected = 2, actual = this[1].paymentUis.size)

                assertEquals(expected = "25 Novembro", actual = this[2].title)
                assertEquals(expected = 1, actual = this[2].paymentUis.size)
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Should emit correct sequence of states when User Logs Out`() = runTest {

        // GIVEN
        authLocalDataSource.saveCredentials(User(
            name = "Test Silva",
            email = "test@email.com",
            picture = "",
            accessToken = "fake_access_token",
            refreshToken = "fake_refresh_token"
        ))
        authLocalDataSource.saveDeviceID(deviceID = Uuid.random().toString())
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 2, month = 11, year = 2025)

        viewModel.uiState.test {

            skipItems(3)
            ensureAllEventsConsumed()

            // WHEN
            viewModel.onEvent(HomeEvent.OnLogoutUser)

            // THEN
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
                assertEquals(expected = false, actual = loggedOut)
            }

            // Verify final state after successful logout
            with(awaitItem()) {
                assertEquals(expected = true, actual = loggedOut)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
            }

            ensureAllEventsConsumed()
        }
    }

}