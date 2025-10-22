package br.com.noartcode.theprice.ui.presentation.account

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import br.com.noartcode.theprice.ui.presentation.account.add.NewAccountEvent
import br.com.noartcode.theprice.ui.presentation.account.add.NewAccountEvent.*
import br.com.noartcode.theprice.ui.presentation.account.add.NewAccountViewModel
import br.com.noartcode.theprice.ui.presentation.bill.add.AddBillEvent
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
import kotlin.getValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NewAccountViewModelTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()

    // Unit Under Test
    private val viewModel: NewAccountViewModel by inject()

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
        cleanupDataStoreFile()
        stopKoin()
        Dispatchers.resetMain()
    }


    @Test
    fun `When ViewModel starts it should return a valid initial ui state`() = runTest {

        viewModel.uiState.test {

            with(awaitItem()) {
                assertEquals(expected = false, emailHasError)
                assertEquals(expected = false, passwordHasError)
                assertEquals(expected = false, canCreate)
            }

            ensureAllEventsConsumed()
        }
    }


    @Test
    fun `When Entered All Required Fields it should enable New Account Creation`() = runTest {

        viewModel.uiState.test {
            skipItems(1)

            viewModel.onEvent(OnNameChanged("Test Silva"))
            assertEquals(expected = false, awaitItem().canCreate)

            viewModel.onEvent(OnEmailChanged("test@email.com"))
            assertEquals(expected = false, awaitItem().canCreate)

            viewModel.onEvent(OnPasswordChanged("123456"))
            assertEquals(expected = true, awaitItem().canCreate)

            ensureAllEventsConsumed()
        }
    }


    @Test
    fun `When Successfully Created User IsCreated Should be true`() = runTest {

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onEvent(OnNameChanged("Test Silva"))
            skipItems(1)
            viewModel.onEvent(OnEmailChanged("test@email.com"))
            skipItems(1)
            viewModel.onEvent(OnPasswordChanged("123456"))
            skipItems(1)

            viewModel.onEvent(OnCreateNewAccount)
            skipItems(1)

            assertEquals(expected = true, awaitItem().isCreated)


            ensureAllEventsConsumed()

        }

    }

}