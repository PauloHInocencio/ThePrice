package br.com.noartcode.theprice.ui.presentation.home

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.domain.usecases.GetPayments
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val database: ThePrinceDatabase by inject()
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val paymentDataSource: PaymentLocalDataSource by lazy { PaymentLocalDataSourceImp(database) }
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter) }
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
                            getTodayDay = get(),
                            getPayments = GetPayments(
                                billLDS = billDataSource,
                                paymentLDS = paymentDataSource,
                                dispatcher = UnconfinedTestDispatcher()
                            ),
                            getMonthName = get(),
                            paymentUiMapper = get(),
                            moveMonth = get(),
                            getFirstPaymentDate = get(),
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

}