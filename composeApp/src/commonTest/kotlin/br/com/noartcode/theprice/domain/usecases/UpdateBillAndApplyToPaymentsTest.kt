package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.domain.usecases.bill.IUpdateBillAndApplyToPayments
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
class UpdateBillAndApplyToPaymentsTest : KoinTest, RobolectricTests() {
    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()

    // Unit Under Test
    private val updateBillAndApplyToPayments: IUpdateBillAndApplyToPayments by inject()

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
    fun `null startDate does not update any payments`() = runTest {
        // GIVEN a bill and startDateToApplyChanges as null
        // WHEN invoking the use case
        // THEN returns Resource.Success, bill is updated, and no payments are modified
    }

    @Test
    fun `updates payments on or after startDate`() = runTest {
        // GIVEN a bill with payments before and after the startDate (e.g., startDate = 2024-06)
        // WHEN invoking the use case with that startDate
        // THEN returns Resource.Success, only payments with dueDate >= startDate are updated with new price and day
    }

    @Test
    fun `propagates bill price to filtered payments`() = runTest {
        // GIVEN a bill with price change and payments after startDate
        // WHEN invoking the use case
        // THEN affected payments have their price updated to match bill price
    }

    @Test
    fun `propagates bill day to filtered payments`() = runTest {
        // GIVEN a bill with billingStartDate.day change and payments after startDate
        // WHEN invoking the use case
        // THEN affected payments have their dueDate.day updated to match bill.billingStartDate.day
    }

    @Test
    fun `marks bill and payments as not synced`() = runTest {
        // GIVEN a bill and payments with isSynced = true
        // WHEN invoking the use case
        // THEN updated bill and affected payments have isSynced = false
    }

    @Test
    fun `updates updatedAt timestamp for bill and payments`() = runTest {
        // GIVEN a bill and payments with old updatedAt timestamps
        // WHEN invoking the use case
        // THEN bill and affected payments have updatedAt set to current time
    }

    @Test
    fun `database error returns error resource`() = runTest {
        // GIVEN a bill that triggers a database exception during update
        // WHEN invoking the use case
        // THEN returns Resource.Error with appropriate error message
    }

    @Test
    fun `no payments exist for bill succeeds`() = runTest {
        // GIVEN a bill with no associated payments
        // WHEN invoking the use case
        // THEN returns Resource.Success with bill updated and empty payments list
    }
}