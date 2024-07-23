package br.com.noartcode.theprice.ui.presentation.newbill

import app.cash.turbine.test
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillEvent
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NewBillViewModelTest : KoinTest {

    private val viewModel:NewBillViewModel by inject()

    @BeforeTest
    fun before() {
        startKoin { modules(platformTestModule()) }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `When the ViewModel started it should return an empty ui state object`()  = runTest{

        // GIVEN
        viewModel.uiState.test {

            // WHEN
            val initialUiState = awaitItem()

            // THEN
            with(initialUiState) {
                assertEquals("", name)
                assertEquals(1, dueDate)
                assertEquals(null, price)
                assertEquals(null, description)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }
    }


    @Test
    fun `State should change when ViewModel receives Events`()  = runTest{

        // GIVEN
        viewModel.uiState.test {

            skipItems(1)

            // WHEN
            with(viewModel){
                onEvent(NewBillEvent.OnNameChanged("internet"))
                skipItems(1)
                onEvent(NewBillEvent.OnPriceChanged(9990))
                skipItems(1)
                onEvent(NewBillEvent.OnDueDateChanged(5))
                skipItems(1)
                onEvent(NewBillEvent.OnDescriptionChanged("Bill of home's internet"))
            }

            // THEN
            with(awaitItem()) {
                assertEquals("internet", name)
                assertEquals("R$ 99,90", price)
                assertEquals(5, dueDate)
                assertEquals("Bill of home's internet", description)
            }

            // Making sure that is not more emissions.
            ensureAllEventsConsumed()
        }
    }

}