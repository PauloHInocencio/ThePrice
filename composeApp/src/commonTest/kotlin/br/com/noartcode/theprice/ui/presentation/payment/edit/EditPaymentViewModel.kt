package br.com.noartcode.theprice.ui.presentation.payment.edit

import app.cash.turbine.test
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.remote.dtos.BillWithPaymentsDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.BillWithPayments
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.bill.IInsertBillWithPayments
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
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
import kotlinx.serialization.json.Json
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
class EditPaymentViewModelTest : KoinTest, RobolectricTests() {
    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val insertBillWithPayments: IInsertBillWithPayments by inject()
    private val eventSyncQueue: EventSyncQueue by inject()

    private val billingStartDate = DayMonthAndYear(day = 15, month = 1, year = 2026)
    private val currentDate = DayMonthAndYear(day = 8, month = 4, year = 2026)

    // Unit Under Test
    private val viewModel: EditPaymentViewModel by inject()


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
    }


    @Test
    fun `When the ViewModel started it should return the correct payment information`() = runTest {

        // GIVEN
        val billWithPayments = initBill(stubBills[0].copy(billingStartDate = billingStartDate))
        // Passing the payment id to the ViewModel SavedStateHandle
        val payment = billWithPayments.payments.last()
        useSavedStateHandle("paymentId" to payment.id)


        // WHEN
        viewModel.uiState.test {

            skipItems(1)

            // THEN
            with(awaitItem()) {
                assertEquals(expected = payment.billId, actual = billId)
                assertEquals(expected = "R$ 120,99", actual = payedValue)
                assertEquals(expected = "15/03/2026", actual = paidDateTitle)
                assertEquals(expected = false, actual = isSaved)
                assertEquals(expected = false, actual = askingConfirmation)
                assertEquals(expected = false, actual = priceHasError)
                assertEquals(expected = false, actual = priceHasChanged)
                assertEquals(expected = false, actual = dateHasChanged)
                assertEquals(expected = false, actual = statusHasChanged)
                assertEquals(expected = null, actual = errorMessage)
            }

            ensureAllEventsConsumed()
        }


    }

    @Test
    fun `When status has changed the confirmation dialog should not appear`() = runTest {

        // GIVEN
        val billWithPayments = initBill(stubBills[0].copy(billingStartDate = billingStartDate))
        // Passing the payment id to the ViewModel SavedStateHandle
        val payment = billWithPayments.payments.last()
        useSavedStateHandle("paymentId" to payment.id)

        viewModel.uiState.test {

            skipItems(2)

            viewModel.onEvent(EditPaymentEvent.OnStatusChanged)

            with(awaitItem()){
                assertEquals(expected = true, actual = statusHasChanged)
                assertEquals(expected = true, actual = canSave)
            }

            viewModel.onEvent(EditPaymentEvent.OnSave)

            with(awaitItem()) {
                assertEquals(expected = false, actual = askingConfirmation)
                assertEquals(expected = true, actual = isSaving)
            }

            with(awaitItem()) {
                assertEquals(expected = false, actual = isSaving)
                assertEquals(expected = true, actual = isSaved)
            }

            ensureAllEventsConsumed()

            val event = eventSyncQueue.peek()!!
            val paymentToSync = Json.decodeFromString<PaymentDto>(event.payload)
            assertEquals(expected = "update", actual = event.action)
            assertEquals(expected = payment.id, actual =  paymentToSync.id)
        }
    }

    @Test
    fun `When price has changed the confirmation dialog should appear`() = runTest {

        // GIVEN
        val billWithPayments = initBill(stubBills[0].copy(billingStartDate = billingStartDate))
        // Passing the payment id to the ViewModel SavedStateHandle
        val payment = billWithPayments.payments.last()
        useSavedStateHandle("paymentId" to payment.id)


        // WHEN
        viewModel.uiState.test {

            skipItems(2)

            viewModel.onEvent(EditPaymentEvent.OnPriceChanged("R$ 120,9"))
            skipItems(1)

            viewModel.onEvent(EditPaymentEvent.OnSave)

            // THEN
            with(awaitItem()) {
                assertEquals(expected = true, actual = askingConfirmation)
                assertEquals(expected = false, actual = isSaving)
                assertEquals(expected = "R$ 12,09", actual = payedValue)
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `When OnChangeAllFuturePayments bill and affected payments should be updated`() = runTest {
        // GIVEN
        val billWithPayments = initBill(stubBills[0].copy(billingStartDate = billingStartDate))
        // Passing the payment id to the ViewModel SavedStateHandle
        val payment = billWithPayments.payments[1]
        useSavedStateHandle("paymentId" to payment.id)

        // WHEN
        viewModel.uiState.test {

            skipItems(2)

            viewModel.onEvent(EditPaymentEvent.OnPriceChanged("R$ 120,9"))
            skipItems(1)

            viewModel.onEvent(EditPaymentEvent.OnChangeAllFuturePayments)
            skipItems(2)

            ensureAllEventsConsumed()

            val event = eventSyncQueue.peek()!!
            val (bill, payments) = Json.decodeFromString<BillWithPaymentsDto>(event.payload)

            assertEquals(expected = 1, eventSyncQueue.size())
            assertEquals(expected = "update", actual = event.action)
            assertEquals(expected = 1209L, actual = bill.price)
            assertEquals(expected = 3, actual = billWithPayments.payments.size)
            assertEquals(expected = 2, actual = payments.size)
            assertTrue { payments.all { it.price == 1209L }}
        }
    }

    @Test
    fun `When OnChangeAllFuturePayments payment status should be update after bill and payments`() = runTest {
        // GIVEN
        val billWithPayments = initBill(stubBills[0].copy(billingStartDate = billingStartDate))
        // Passing the payment id to the ViewModel SavedStateHandle
        val payment = billWithPayments.payments[1]
        useSavedStateHandle("paymentId" to payment.id)

        // WHEN
        viewModel.uiState.test {

            skipItems(2)

            viewModel.onEvent(EditPaymentEvent.OnPriceChanged("R$ 120,9"))
            skipItems(1)

            viewModel.onEvent(EditPaymentEvent.OnStatusChanged)
            skipItems(1)

            viewModel.onEvent(EditPaymentEvent.OnChangeAllFuturePayments)
            skipItems(2)

            ensureAllEventsConsumed()

            val paymentEvent = eventSyncQueue.peek()!!
            val paymentToSync = Json.decodeFromString<PaymentDto>(paymentEvent.payload)

            assertEquals(expected = 2, eventSyncQueue.size()) // two events
            assertEquals(expected = "update", actual = paymentEvent.action)
            assertEquals(expected = true, actual = paymentToSync.isPayed)

            eventSyncQueue.remove(paymentEvent.id)
            val billWithPaymentsEvent = eventSyncQueue.peek()!!
            val (_, payments) = Json.decodeFromString<BillWithPaymentsDto>(billWithPaymentsEvent.payload)

            assertEquals(expected = "update", actual = billWithPaymentsEvent.action)
            assertEquals(expected = 2, payments.size)
            assertEquals(expected = true, payments[0].isPayed)
            assertEquals(expected = false, payments[1].isPayed)
        }
    }


    private suspend fun initBill(bill: Bill) : BillWithPayments {
        // Populating database with 1 bill.
        return (insertBillWithPayments(
            bill = bill,
            currentDate = currentDate
        ) as Resource.Success).data

    }

}