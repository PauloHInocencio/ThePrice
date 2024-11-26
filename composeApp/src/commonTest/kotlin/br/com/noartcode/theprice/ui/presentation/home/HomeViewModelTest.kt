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
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
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
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.math.exp
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest : KoinTest, RobolectricTests() {

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
            dispatcher = UnconfinedTestDispatcher()
        )
    }

    // Unit Under Test
    private val viewModel:HomeViewModel by inject()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
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
                                dispatcher = UnconfinedTestDispatcher()
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
        Dispatchers.resetMain()
        stopKoin()
        database.close()
    }


    @Test
    fun `When the ViewModel started it should return an empty state object`() = runTest {

        // GIVEN
        viewModel.uiState.test {

            with(awaitItem()) {
                assertEquals(emptyList(), payments)
                assertEquals("", monthName)
                assertEquals(false, loading)
                assertEquals(null, errorMessage)
            }

            ensureAllEventsConsumed()

        }
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

        // GIVEN
        viewModel.uiState.test {

            // Current month should be November
            skipItems(1)
            with(awaitItem()) {
                assertEquals("Novembro - 2024", monthName)
                assertEquals(expected = true, actual = canGoBack)
            }

            // Go Back to October
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)
            skipItems(3)

            // Should display the correct month and allow to go back to another month
            with(awaitItem()) {
                assertEquals("Outubro - 2024", monthName)
                assertEquals(expected = true, actual = canGoBack)
            }

            // Go back to September
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)
            skipItems(3)

            // Should display the correct month and NOT allow to go back another month
            with(awaitItem()) {
                assertEquals("Setembro - 2024", monthName)
                assertEquals(expected = false, actual = canGoBack)
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

            // ignoring the first emission
            skipItems(1)

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
    fun `When a Bill is Updated Only Pending Payments Of The Current Month Should Be Affected`() = runTest{
        // Populating the database with 1 bill and 3 paid payments
        val bill = Bill(
            name = "Internet",
            description = "My internet bill.",
            price = 10099,
            billingStartDate = DayMonthAndYear(day = 5, month = 9, year = 2024)
        )
        val billId = populateDBWithAnBillAndFewPayments(
            bill = bill,
            billingStartDate = DayMonthAndYear(day = 5, month = 8, year = 2024),
            numOfPayments = 3,
            billDataSource,
            paymentDataSource
        )

        // set the current date
        getTodayDate.date =  DayMonthAndYear(day = 21, month = 11, year = 2024)


        // GIVEN
        viewModel.uiState.test {

            // ignoring emissions
            skipItems(2)

            // change amount of bill
            billDataSource.update( bill.copy(id = billId, price = 12099) )

            // check if the current payment has changed
            with(awaitItem()) {
                val payment = payments.first()
                assertEquals(expected = 4, actual = payment.id)
                assertEquals(expected = "R$ 120,99", actual = payment.price)
                assertEquals(expected = "OVERDUE", actual = payment.status.name)

            }

            // Go back to previous month
            viewModel.onEvent(HomeEvent.OnBackToPreviousMonth)
            skipItems(3)

            // checking if previous paid bill remains the same

            with(awaitItem()) {
                val payment = payments.first()
                assertEquals(expected = 3, actual = payment.id)
                assertEquals(expected = "PAYED", payment.status.name)
                assertEquals(expected = "R$ 100,99", actual = payment.price)
            }


            ensureAllEventsConsumed()

            // simulating the click on a paid payment to change it status
            viewModel.onEvent(HomeEvent.OnPaymentStatusClicked(
                id = 3,
                status = PaymentUi.Status.PAYED,
                price = "R$ 100,99"
            ))

            skipItems(2)

            with(awaitItem()){
                val payment = payments.first()
                assertEquals(expected = 3, actual = payment.id)
                assertEquals(expected = "OVERDUE", payment.status.name)
                assertEquals(expected = "R$ 100,99", actual = payment.price)
            }
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

            // ignoring emissions
            skipItems(2)


            // simulating the click on a paid payment to change it status
            viewModel.onEvent(HomeEvent.OnPaymentStatusClicked(
                id = 4,
                status = PaymentUi.Status.OVERDUE,
                price = "R$ 120,99"
            ))

            skipItems(2)

            with(awaitItem()){
                val payment = payments.first()
                assertEquals(expected = 4, payment.id)
                assertEquals(expected = "PAYED", payment.status.name)
                assertEquals(expected = "R$ 120,99", actual = payment.price)
            }
        }
    }


}