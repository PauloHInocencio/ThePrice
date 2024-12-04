package br.com.noartcode.theprice.ui.presentation.home

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.populateDBWithAnBillAndFewPayments
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.GetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.IUpdatePaymentStatus
import br.com.noartcode.theprice.domain.usecases.UpdatePayment
import br.com.noartcode.theprice.domain.usecases.UpdatePaymentStatus
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.mapper.PaymentDomainToUiMapper
import br.com.noartcode.theprice.ui.presentation.home.model.HomeEvent
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : KoinTest, RobolectricTests() {


    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val paymentDataSource: PaymentLocalDataSource by lazy { PaymentLocalDataSourceImp(database) }
    private val getTodayDate: GetTodayDateStub = GetTodayDateStub()
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter, getTodayDate) }
    private val formatter: ICurrencyFormatter by inject()
    private val dateFormat: IGetDateFormat by inject()
    private val getDaysUntil: IGetDaysUntil by inject()
    private val uiMapper: PaymentDomainToUiMapper by lazy {
        PaymentDomainToUiMapper(
            dataSource = billDataSource,
            formatter = formatter,
            getTodayDate = getTodayDate,
            dateFormat = dateFormat,
            getDaysUntil = getDaysUntil,
        )
    }
    private val getFirstPaymentDate: IGetOldestPaymentRecordDate by lazy {
        GetOldestPaymentRecordDate(localDataSource = billDataSource, epochFormatter = epochFormatter)
    }
    private val updatePayment: IUpdatePaymentStatus by lazy {
        UpdatePaymentStatus(
            datasource = paymentDataSource,
            dispatcher = testDispatcher
        )
    }

    // Unit Under Test
    private val viewModel:HomeViewModel by inject()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        startKoin{
            modules(
                commonTestModule(),
                platformTestModule(),
                module {
                    viewModel {
                        HomeViewModel(
                            getTodayDay = getTodayDate,
                            getPayments = GetPayments(
                                billLDS = billDataSource,
                                paymentLDS = paymentDataSource,
                                dispatcher = testDispatcher
                            ),
                            getMonthName = get(),
                            paymentUiMapper = uiMapper,
                            moveMonth = get(),
                            getFirstPaymentDate = getFirstPaymentDate,
                            updatePaymentStatus = updatePayment,
                        )
                    }
                }
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
    fun `Should emit correct sequence of state on the ViewModel initialization`() = runTest {


        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            populateDBWithAnBillAndFewPayments(
                bill = stubBills[i],
                billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024),
                numOfPayments = 0,
                billDataSource = billDataSource,
                paymentDataSource = paymentDataSource
            )
        }


        // Set Current Day to November 21th
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test{

            // check initial state
            with(awaitItem()) {
                assertEquals(expected = 0, actual = payments.size)
                assertEquals(expected = "", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }

            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = 0, actual = payments.size)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = true, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }


            // Final State
            with(awaitItem()) {
                assertEquals(expected = 3, actual = payments.size)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }


            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `Should emit correct sequence of states when User Went back to Previous Month`() = runTest {


        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 9
            populateDBWithAnBillAndFewPayments(
                bill = stubBills[i],
                billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024),
                numOfPayments = 0,
                billDataSource = billDataSource,
                paymentDataSource = paymentDataSource
            )
        }


        // Set Current Day to November 21th
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test{

            // Ignoring empty state
            skipItems(1)

            // Start Loading Payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Final State
            with(awaitItem()) {
                assertEquals(expected = 3, actual = payments.size)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
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
                assertEquals(expected = 2, actual = payments.size)
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
                assertEquals(expected = 1, actual = payments.size)
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
            populateDBWithAnBillAndFewPayments(
                bill = stubBills[i],
                billingStartDate = DayMonthAndYear(day = 12, month = startMonth + i, year = 2024),
                numOfPayments = 0,
                billDataSource = billDataSource,
                paymentDataSource = paymentDataSource
            )
        }

        // Set Current Day to November 21th
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test {

            // Ignoring states
            skipItems(2)

            // Final State
            val result = awaitItem()
            with(result) {
                assertEquals(expected = 3, actual = payments.size)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }

            ensureAllEventsConsumed()

            // Check payment info
            val originalPayment = assertNotNull(result.payments.find { it.billName == "mom's internet" })
            assertEquals(expected = PaymentUi.Status.OVERDUE, originalPayment.status)

            // Simulate user changing payment status
            viewModel.onEvent(HomeEvent.OnPaymentStatusClicked(
                id = originalPayment.id,
                status = originalPayment.status,
            ))

            // Check final state
            val newResult = awaitItem()
            with(newResult) {
                assertEquals(expected = 3, actual = payments.size)
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = canGoNext)
            }

            ensureAllEventsConsumed()

            // Confirm that selected payment has changed
            val updatedPayment = assertNotNull(newResult
                .payments
                .find { it.billName == "mom's internet" })

            assertEquals(expected = originalPayment.id, actual = updatedPayment.id)
            assertEquals(expected = PaymentUi.Status.PAYED, updatedPayment.status)

        }

    }

    @Test
    fun `Payment Amount and Due Date Should Not Change when User Update Payment Status`() = runTest {

        // Populating the database with 1 bill
        populateDBWithAnBillAndFewPayments(
            bill = stubBills[1],
            billingStartDate = DayMonthAndYear(day = 12, month = 9, year = 2024),
            numOfPayments = 0,
            billDataSource = billDataSource,
            paymentDataSource = paymentDataSource
        )

        // Set Current Day to November 21th
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)

        viewModel.uiState.test {

            // Ignoring states
            skipItems(2)

            val originalPayment = assertNotNull(awaitItem()
                .payments
                .find { it.billName == "mom's internet" })

            ensureAllEventsConsumed()

            with(originalPayment) {
                assertEquals(expected = PaymentUi.Status.OVERDUE, actual =  status)
                assertEquals(expected = "R$ 99,99", actual = price)
                assertEquals(expected = "9 days overdue", actual = statusDescription)
            }

            // Simulate user changing payment status
            viewModel.onEvent(HomeEvent.OnPaymentStatusClicked(
                id = originalPayment.id,
                status = originalPayment.status,
            ))


            // Confirm the new status
            with(awaitItem()) {
                val payment = payments.find { it.billName == "mom's internet" }
                assertEquals(PaymentUi.Status.PAYED, payment?.status)
            }

            ensureAllEventsConsumed()

            // Simulate user changing payment info
            paymentDataSource.updatePayment(
                id = originalPayment.id,
                price = 10000,
                dueDate = DayMonthAndYear(day = 5, month = 11, year = 2024),
                isPayed = false,
            )


            // Get the update version of the payment
            val updatedPayment = assertNotNull(awaitItem()
                .payments
                .find { it.billName == "mom's internet" })


            ensureAllEventsConsumed()

            with(updatedPayment) {
                assertEquals(expected = PaymentUi.Status.OVERDUE, status)
                assertEquals(expected = "R$ 100,00", actual = price)
                assertEquals(expected = "9 days overdue", actual = statusDescription)
            }
        }
    }


}