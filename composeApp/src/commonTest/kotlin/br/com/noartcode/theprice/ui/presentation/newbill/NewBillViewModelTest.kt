package br.com.noartcode.theprice.ui.presentation.newbill

import app.cash.turbine.test
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.presentation.newbill.model.NewBillEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val viewModel:NewBillViewModel by inject()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        startKoin { modules(commonTestModule(),  platformTestModule()) }
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
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
                assertEquals("R$ 0,00", price)
                assertEquals(null, description)
            }

        }
    }


    @Test
    fun `State should change when ViewModel receives Events`()  = runTest{

        // GIVEN
        viewModel.uiState.test {

            // WHEN
            with(viewModel){
                onEvent(NewBillEvent.OnNameChanged("internet"))
                onEvent(NewBillEvent.OnPriceChanged("9990"))
                onEvent(NewBillEvent.OnDueDateChanged(5))
                onEvent(NewBillEvent.OnDescriptionChanged("Bill of home's internet"))
                skipItems(4)
            }

            // THEN
            with(awaitItem()) {
                assertEquals("internet", name)
                assertEquals("R$ 99,90", price)
                assertEquals(5, dueDate)
                assertEquals("Bill of home's internet", description)
            }
        }
    }

    @Test
    fun `OnSaveEvent should add the new bill into the database`() = runTest {

        // GIVEN
        viewModel.uiState.test {

            // WHEN
            with(viewModel){
                onEvent(NewBillEvent.OnNameChanged("internet"))
                onEvent(NewBillEvent.OnPriceChanged("9990"))
                onEvent(NewBillEvent.OnDueDateChanged(5))
                onEvent(NewBillEvent.OnDescriptionChanged("Bill of home's internet"))
                skipItems(5)
            }


            viewModel.onEvent(NewBillEvent.OnSave)

            // THEN
            with(awaitItem()) {
                assertEquals(true, isSaving)
                assertEquals(false, isSaved)
            }

            with(awaitItem()) {
                assertEquals(false, isSaving)
                assertEquals(true, isSaved)
            }
        }
    }
}