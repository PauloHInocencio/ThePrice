package br.com.noartcode.theprice.ui.presentation.newbill

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import br.com.noartcode.theprice.ui.presentation.bill.add.AddBillEvent
import br.com.noartcode.theprice.ui.presentation.bill.add.AddBillViewModel
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
class NewBillViewModelTest : KoinTest, RobolectricTests() {
    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()

    // Unit Under Test
    private val viewModel: AddBillViewModel by inject()

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
    fun `When the ViewModel started it should return an empty ui state object`()  = runTest{

        // GIVEN
        viewModel.uiState.test {

            skipItems(1)

            // THEN
            with(awaitItem()) {
                assertEquals("", name)
                assertEquals("R$ 0,00", price)
                assertEquals(null, description)
            }

            ensureAllEventsConsumed()
        }
    }


    @Test
    fun `State should change when ViewModel receives Events`()  = runTest{

        // GIVEN
        viewModel.uiState.test {

            // ignoring the emissions
            skipItems(2)

            // changing the bill name
            viewModel.onEvent(AddBillEvent.OnNameChanged("internet"))
            skipItems(1)

            //changing the bill price
            viewModel.onEvent(AddBillEvent.OnPriceChanged("9990"))
            skipItems(1)

            // changing the bill description
            viewModel.onEvent(AddBillEvent.OnDescriptionChanged("Bill of home's internet"))
            with(awaitItem()) {
                assertEquals("internet", name)
                assertEquals("R$ 99,90", price)
                assertEquals("Bill of home's internet", description)
            }

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `OnSaveEvent should add the new bill into the database`() = runTest {

        // GIVEN
        viewModel.uiState.test {

            // ignoring the emissions
            skipItems(2)

            // changing the bill name
            viewModel.onEvent(AddBillEvent.OnNameChanged("internet"))
            skipItems(1)

            //changing the bill price
            viewModel.onEvent(AddBillEvent.OnPriceChanged("9990"))
            skipItems(1)

            // changing the bill description
            viewModel.onEvent(AddBillEvent.OnDescriptionChanged("Bill of home's internet"))
            with(awaitItem()) {
                assertEquals(expected = true, canSave)
                assertEquals("internet", name)
                assertEquals("R$ 99,90", price)
                assertEquals("Bill of home's internet", description)
            }


            viewModel.onEvent(AddBillEvent.OnSave)

            // THEN
            with(awaitItem()) {
                assertEquals(true, isSaving)
                assertEquals(false, isSaved)
            }

            with(awaitItem()) {
                assertEquals(false, isSaving)
                assertEquals(true, isSaved)
            }

            ensureAllEventsConsumed()
        }
    }



    @Test
    fun `When User Typed Wrong Price Input ViewModel Should Reset Field`() = runTest {

        viewModel.uiState.test {

            // ignoring the emissions
            skipItems(2)

            // entering the limit amount
            viewModel.onEvent(AddBillEvent.OnPriceChanged("9223372036854775807"))
            with(awaitItem()) {
                assertEquals(expected = "R$ 92.233.720.368.547.758,07", price)
            }

            // exceeding the limit
            viewModel.onEvent(AddBillEvent.OnPriceChanged("9223372036854775808"))
            with(awaitItem()) {
                assertEquals(expected = true, actual = priceHasError)
                assertEquals(expected = "R$ 0,00", actual = price)
            }

            ensureAllEventsConsumed()
        }
    }
}