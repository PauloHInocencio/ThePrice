package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class GetOldestPaymentRecordDateTest:KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val billDataSource: BillLocalDataSource by inject()

    // Unit Under Test
    private val getFirstPaymentDate:IGetOldestPaymentRecordDate by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
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
    fun `When more the one bill have the same start date Return at least one of them`() = runTest {
        // GIVEN
        billDataSource.apply {
            repeat(3) {
                insert(bill = stubBills[0])
            }
        }


        // check if bills were inserted correctly
       val bills = billDataSource.getAllBills().first()
        assertEquals(
            expected = 3,
            actual = bills.size
        )

        //WHEN
        getFirstPaymentDate().test {
            with(awaitItem()) {
                assertEquals(9, this?.month)
            }
        }
    }


    @Test
    fun `When oldest bills have the same start month and year Return the one with the smallest day`() = runTest {

        with(billDataSource) {
            val bill = stubBills[0]
            repeat(3) { i ->
                insert(
                    bill.copy(billingStartDate =  bill.billingStartDate.copy(day = i + 1)),
                )
            }
        }

        val date = getFirstPaymentDate().first()
        assertEquals(
            expected = 1,
            actual = date?.day
        )
    }

    @Test
    fun `Should return the oldest bill record from the database`() = runTest {

        // GIVEN
        billDataSource.apply {
            repeat(3) { i ->
                insert(bill = stubBills[i])
            }
        }


        //WHEN
        getFirstPaymentDate().test {
            with(awaitItem()) {
                assertEquals(7, this?.month)
            }
        }
    }

}