package br.com.noartcode.theprice.ui.presentation.newbill

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NewBillViewModelTest {

    private val viewModel = NewBillViewModel()

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
        }
    }

}