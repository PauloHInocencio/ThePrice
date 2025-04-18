package br.com.noartcode.theprice.ui.presentation.home

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.runComposeUiTest
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class HomeScreenTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val paymentDataSource: PaymentLocalDataSource by inject()
    private val billDataSource: BillLocalDataSource by inject()
    private val viewModel:HomeViewModel by inject()


    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        startKoin{
            modules(
                platformTestModule(),
                commonModule(),
                commonTestModule(testDispatcher),
                viewModelsModule()
            )
        }
    }


    @AfterTest
    fun after(){
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }


    @Test
    fun `Screen Should Display Correct Initial State`() = runComposeUiTest {
        setContent {
            HomeScreen(
                state = viewModel.uiState.collectAsState().value,
                onEvent = viewModel::onEvent,
                onNavigateToAddBill = {},
                onNavigateToEditPayment = {}
            )
        }
        waitForIdle()
        onNodeWithContentDescription("Bills of December 2024")
            .assertExists()
    }
}