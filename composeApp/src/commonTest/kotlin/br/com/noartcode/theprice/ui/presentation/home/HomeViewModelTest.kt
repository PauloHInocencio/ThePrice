package br.com.noartcode.theprice.ui.presentation.home

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
import br.com.noartcode.theprice.domain.usecases.GetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetOldestPaymentRecordDate
import br.com.noartcode.theprice.domain.usecases.IUpdatePayment
import br.com.noartcode.theprice.domain.usecases.UpdatePayment
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.mapper.PaymentDomainToUiMapper
import br.com.noartcode.theprice.ui.presentation.home.model.HomeEvent
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
    private val updatePayment: IUpdatePayment by lazy {
        UpdatePayment(
            datasource = paymentDataSource,
            currencyFormatter = formatter,
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
                            updatePayment = updatePayment,
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
    fun `When Current Month is the Oldest Billing Start Date canGoBack Property Should be false `() = runTest {

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

        // Set Current Day to November 21th
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        viewModel.uiState.test {

            // check initial state
            with(awaitItem()) {
                assertEquals(expected = emptyList(), actual = payments)
                assertEquals(expected = "", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
            }

            // loading payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // display November list
            with(awaitItem()) {
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected = false, actual = loading)
            }

            // Go Back to October
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)

            // loading payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Should display the correct month and allow to go back to another month
            with(awaitItem()) {
                assertEquals(expected = "Outubro - 2024", actual = monthName)
                assertEquals(expected = true, actual = canGoBack)
                assertEquals(expected= false, actual = loading)
            }

            // Go back to September
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)

            // loading payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Should display the correct month and NOT allow to go back another month
            with(awaitItem()) {
                assertEquals("Setembro - 2024", monthName)
                assertEquals(expected = false, actual = canGoBack)
                assertEquals(expected= false, actual = loading)
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `When the DataBase is Populate ViewModel should Display the Correct State`() = runTest {

        // Populating the database with 3 different bills
        repeat(3) { i ->
            val startMonth = 8
            populateDBWithAnBillAndFewPayments(
                bill = stubBills[i],
                billingStartDate = DayMonthAndYear(day = 5, month = startMonth + i, year = 2024),
                numOfPayments = 0,
                billDataSource,
                paymentDataSource
            )
        }

        // set the current date
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)

        // GIVEN
        viewModel.uiState.test {

            // check initial state
            with(awaitItem()) {
                assertEquals(expected = emptyList(), actual = payments)
                assertEquals(expected = "", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
            }

            // loading payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Display correct payments
            with(awaitItem()) {
                assertEquals(3, payments.size)
                assertEquals("Novembro - 2024", monthName)
                assertEquals(false, loading)
                assertEquals(null, errorMessage)
                assertEquals(true, canGoBack)
            }

            ensureAllEventsConsumed()

        }


    }

    @Test
    fun `When a Payed bill Receive the Event OnPaymentStatusClicked it Status Should Change`() = runTest {
        populateDBWithAnBillAndFewPayments(
            bill = stubBills[0],
            billingStartDate = DayMonthAndYear(day = 5, month = 8, year = 2024),
            numOfPayments = 3,
            billDataSource,
            paymentDataSource
        )

        // set the current date
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)

        viewModel.uiState.test {

            // check initial state
            with(awaitItem()) {
                assertEquals(expected = emptyList(), actual = payments)
                assertEquals(expected = "", actual = monthName)
                assertEquals(expected = false, actual = loading)
                assertEquals(expected = null, actual = errorMessage)
            }

            // loading payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Display correct payments
            with(awaitItem()) {
                assertEquals(expected = 1, actual = payments.size)
                assertEquals(expected = "Novembro - 2024", actual = monthName)

                val payment = payments.first()
                assertEquals(expected = 4, payment.id)
                assertEquals(expected = "OVERDUE", payment.status.name)
                assertEquals(expected = "R$ 120,99", actual = payment.price)
            }


            // simulating the click on a paid payment to change it status
            viewModel.onEvent(HomeEvent.OnPaymentStatusClicked(
                id = 4,
                status = PaymentUi.Status.OVERDUE,
                price = "R$ 120,99"
            ))

            // loading payments
            with(awaitItem()) {
                assertEquals(expected = true, actual = loading)
            }

            // Check for the payment update
            with(awaitItem()){
                assertEquals(expected = "Novembro - 2024", actual = monthName)
                assertEquals(expected = false, actual = loading)

                val payment = payments.first()
                assertEquals(expected = 4, payment.id)
                assertEquals(expected = "PAYED", payment.status.name)
                assertEquals(expected = "R$ 120,99", actual = payment.price)
            }

            ensureAllEventsConsumed()
        }
    }


}