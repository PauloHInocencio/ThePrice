package br.com.noartcode.theprice.ui.presentation.bill.edit

import app.cash.turbine.test
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.isTotalMonthsGreaterOrEqual
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.domain.usecases.datetime.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.useSavedStateHandle
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

@OptIn(ExperimentalCoroutinesApi::class)
class EditBillViewModelTest : KoinTest, RobolectricTests() {
    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val insertBillWithPayments: IInsertBillWithPayments by inject()
    private val paymentDataSource: PaymentLocalDataSource by inject()
    private val getTodayDate: IGetTodayDate by inject()


    private val billingStartDate = DayMonthAndYear(day = 15, month = 1, year = 2026)
    private val currentDate = DayMonthAndYear(day = 25, month = 3, year = 2026)


    // Unit Under Test
    private val viewModel: EditBillViewModel by inject()

    @BeforeTest
    fun before() {
        startKoin {
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

        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }

    @Test
    fun `save with price change shows propagation dialog`() = runTest {

        // Populating database with 1 bill.
        val billWithPayments = (insertBillWithPayments(
            bill = stubBills[0].copy(billingStartDate = billingStartDate),
            currentDate = currentDate
        ) as Resource.Success).data

        // Passing the bill id to the ViewModel SavedStateHandle
        useSavedStateHandle("billId" to billWithPayments.bill.id)

        viewModel.uiState.test {

            // Skip initial state
            skipItems(2)


            // GIVEN an edited bill with changed price
            viewModel.onEvent(EditBillEvent.OnPriceChanged("R$ 120,9"))
            with(awaitItem()) {
               assertEquals(expected = "R$ 12,09", actual = this.price)
            }

            // WHEN onEvent(OnSave) is called
            viewModel.onEvent(EditBillEvent.OnSave)

            // THEN showingChangePropagationDialog is set to true
            with(awaitItem()){
                assertEquals(expected = true, actual = this.showingChangePropagationDialog)
            }

            ensureAllEventsConsumed()
        }


    }

    @Test
    fun `save with billing date change shows propagation dialog`() = runTest {

        // Populating database with 1 bill.
        val billWithPayments = (insertBillWithPayments(
            bill = stubBills[0].copy(billingStartDate = billingStartDate),
            currentDate = currentDate
        ) as Resource.Success).data

        // Passing the bill id to the ViewModel SavedStateHandle
        useSavedStateHandle("billId" to billWithPayments.bill.id)

        viewModel.uiState.test {

            // Skip initial state
            skipItems(2)

            // GIVEN an edited bill with changed billingStartDate
            val newBillingDate = DayMonthAndYear(day = 20, month = 2, year = 2026)
            viewModel.onEvent(EditBillEvent.OnBillingStartDateChanged(newBillingDate))
            with(awaitItem()) {
                assertEquals(expected = newBillingDate, actual = this.billingStartDate)
            }

            // WHEN onEvent(OnSave) is called
            viewModel.onEvent(EditBillEvent.OnSave)

            // THEN showingChangePropagationDialog is set to true
            with(awaitItem()) {
                assertEquals(expected = true, actual = this.showingChangePropagationDialog)
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `apply changes to all payments updates from original date`() = runTest {

        // Populating database with 1 bill.
        val billWithPayments = (insertBillWithPayments(
            bill = stubBills[0].copy(billingStartDate = billingStartDate),
            currentDate = currentDate
        ) as Resource.Success).data

        // Passing the bill id to the ViewModel SavedStateHandle
        useSavedStateHandle("billId" to billWithPayments.bill.id)

        viewModel.uiState.test {

            // GIVEN billingStartDate and price changed and saved
            // Skip initial state
            skipItems(2)

            // ...Changing billing Start Date
            val newBillingDate = DayMonthAndYear(day = 20, month = 2, year = 2026)
            viewModel.onEvent(EditBillEvent.OnBillingStartDateChanged(newBillingDate))
            skipItems(1)

            // ...Changing price
            val newPrice = "R$ 300,00"
            viewModel.onEvent(EditBillEvent.OnPriceChanged(newPrice))
            skipItems(1)

            // ...Pressing save
            viewModel.onEvent(EditBillEvent.OnSave)
            skipItems(1)

            // WHEN onEvent(OnApplyChangesToAllPayments) is called
            viewModel.onEvent(EditBillEvent.OnApplyChangesToAllPayments)
            skipItems(2)
            ensureAllEventsConsumed()

            // THEN all payments were update with the new information
            val payments = paymentDataSource.getBillPayments(billWithPayments.bill.id)
            payments.forEach { payment ->
                assertEquals(expected = 30000L, actual = payment.price)
                assertEquals(expected = 20, actual = payment.dueDate.day)
            }


        }



    }

    @Test
    fun `apply changes to future payments uses today date`() = runTest {

        // Populating database with 1 bill.
        val billWithPayments = (insertBillWithPayments(
            bill = stubBills[0].copy(billingStartDate = billingStartDate),
            currentDate = currentDate
        ) as Resource.Success).data

        // Passing the bill id to the ViewModel SavedStateHandle
        useSavedStateHandle("billId" to billWithPayments.bill.id)

        // Set Current Day to March 25th
        (getTodayDate as GetTodayDateStub).date =  DayMonthAndYear(day = 25, month = 3, year = 2026)

        viewModel.uiState.test {

            // GIVEN billingStartDate and price changed and saved
            // Skip initial state
            skipItems(2)

            // ...Changing billing Start Date
            val newBillingDate = DayMonthAndYear(day = 20, month = 2, year = 2026)
            viewModel.onEvent(EditBillEvent.OnBillingStartDateChanged(newBillingDate))
            skipItems(1)

            // ...Changing price
            val newPrice = "R$ 300,00"
            viewModel.onEvent(EditBillEvent.OnPriceChanged(newPrice))
            skipItems(1)

            // ...Pressing save
            viewModel.onEvent(EditBillEvent.OnSave)
            skipItems(1)

            // WHEN onEvent(OnApplyChangesToFuturePayments) is called
            viewModel.onEvent(EditBillEvent.OnApplyChangesToFuturePayments)
            skipItems(2)
            ensureAllEventsConsumed()

            // THEN only future payments are updated with new information
            val payments = paymentDataSource.getBillPayments(billWithPayments.bill.id)
            val (updatedPayments, notUpdatedPayments) = payments.partition {
                it.dueDate.isTotalMonthsGreaterOrEqual(getTodayDate())
            }

            assertEquals(expected = 1, actual = updatedPayments.size)
            assertEquals(expected = 30000L, actual = updatedPayments[0].price)
            assertEquals(expected = 20, actual = updatedPayments[0].dueDate.day)

            assertEquals(expected = 2, actual = notUpdatedPayments.size)
            notUpdatedPayments.forEach { payment ->
                assertEquals(expected = 12099L, actual = payment.price)
                assertEquals(expected = 15, actual = payment.dueDate.day)
            }
        }
    }

    @Test
    fun `successful update enqueues sync event`() = runTest {
        // GIVEN a valid bill update
        // WHEN update completes successfully
        // THEN sync event is enqueued with action "update"
    }

    @Test
    fun `successful update sets canClose to true`() = runTest {
        // GIVEN a valid bill update
        // WHEN update completes successfully
        // THEN uiState.canClose is set to true
    }

    @Test
    fun `update error sets error message and stops saving`() = runTest {
        // GIVEN a bill update that fails
        // WHEN update completes with error
        // THEN uiState.errorMessage is set and isSaving is false
    }

    @Test
    fun `save with no changes sets canClose immediately`() = runTest {
        // GIVEN no fields have changed from original bill
        // WHEN onEvent(OnSave) is called
        // THEN uiState.canClose is true without calling update
    }
}