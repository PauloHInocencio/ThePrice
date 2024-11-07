package br.com.noartcode.theprice.ui.presentation.newbill

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.InsertOrReplaceBill
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.presentation.bill.add.AddBillViewModel
import br.com.noartcode.theprice.ui.presentation.bill.add.model.AddBillEvent
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
class NewBillViewModelTest : KoinTest, RobolectricTests() {

    private val database: ThePriceDatabase by inject()
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter) }
    private val viewModel: AddBillViewModel by inject()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        startKoin {
            modules(
                commonTestModule(),
                platformTestModule(),
                module {
                    viewModel {
                        AddBillViewModel(
                            currencyFormatter = get(),
                            insertNewBill = InsertOrReplaceBill(
                                localDataSource = billDataSource,
                                dispatcher = UnconfinedTestDispatcher(),
                            ),
                            getTodayDate = get(),
                            epochFormatter = get(),
                            getMonthName = get(),
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
    fun `When the ViewModel started it should return an empty ui state object`()  = runTest{

        // GIVEN
        viewModel.uiState.test {

            // WHEN
            val initialUiState = awaitItem()

            // THEN
            with(initialUiState) {
                assertEquals("", name)
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
                onEvent(AddBillEvent.OnNameChanged("internet"))
                onEvent(AddBillEvent.OnPriceChanged("9990"))
                onEvent(AddBillEvent.OnDescriptionChanged("Bill of home's internet"))
                skipItems(4)
            }

            // THEN
            with(awaitItem()) {
                assertEquals("internet", name)
                assertEquals("R$ 99,90", price)
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
                onEvent(AddBillEvent.OnNameChanged("internet"))
                onEvent(AddBillEvent.OnPriceChanged("9990"))
                onEvent(AddBillEvent.OnDescriptionChanged("Bill of home's internet"))
                skipItems(5)
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
        }
    }
}