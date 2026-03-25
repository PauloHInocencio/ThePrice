package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBillWithPayments
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
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

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateBillWithPaymentsTest : KoinTest, RobolectricTests() {
    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()

    // Unit Under Test
    private val updateBillWithPayments: IUpdateBillWithPayments by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule()
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
    fun `successful update returns success with billWithPayments`() = runTest {
        // GIVEN a valid BillWithPayments object
        // WHEN invoking the use case
        // THEN returns Resource.Success containing the updated BillWithPayments and the data is persisted in the database
    }

    @Test
    fun `database error returns error resource`() = runTest {
        // GIVEN a BillWithPayments object that triggers a database exception
        // WHEN invoking the use case
        // THEN returns Resource.Error with appropriate error message
    }

    @Test
    fun `empty payments list succeeds`() = runTest {
        // GIVEN a BillWithPayments with an empty payments list
        // WHEN invoking the use case
        // THEN returns Resource.Success and bill is updated without errors
    }
}