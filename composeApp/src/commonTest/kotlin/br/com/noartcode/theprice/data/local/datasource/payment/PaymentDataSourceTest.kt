package br.com.noartcode.theprice.data.local.datasource.payment

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
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
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentDataSourceTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val billDataSource: BillLocalDataSource by inject()

    // Unit Under Test
    private val paymentDataSource: PaymentLocalDataSource by inject()


    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                commonTestModule(testDispatcher),
                viewModelsModule()
            )
        }
    }

    @AfterTest
    fun after() {
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `Should successfully add new items into the database`() = runTest {
        val bill = stubBills[0]
        val id = billDataSource.insert(bill = stubBills[0])


        assertEquals(expected = 1, paymentDataSource.
            insert(
                billID = id, dueDate = DayMonthAndYear( day = 5, month = 1, year = 2024),
                price = bill.price, isPayed = false
            )
        )
        assertEquals(expected = 2, paymentDataSource.
            insert(
                billID = id, dueDate = DayMonthAndYear( day = 5, month = 2, year = 2024),
                price = bill.price, isPayed = false
            )
        )
        assertEquals(expected = 3, paymentDataSource.
            insert(
                billID = id, dueDate = DayMonthAndYear(day = 5, month = 3, year = 2024),
                price = bill.price, isPayed = false
            )
        )
        assertEquals(expected = 3, paymentDataSource.getBillPayments(id).size)
    }


    @Test
    fun `When updating an existing bill previous bills paid shouldn't changed`() = runTest {

        // Adding payments for a bill
        val bill = stubBills[0]
        val billId = billDataSource.insert(bill)
        val paidValue = bill.price
        val numOfPayments = 3
        with(paymentDataSource) {
            repeat(numOfPayments) { i ->
                val paymentMonth = bill.billingStartDate.month + i
                insert(
                    billID = billId,
                    dueDate = DayMonthAndYear(day = 5, month = paymentMonth, year = 2024),
                    price = paidValue,
                    isPayed = true,
                )
            }
        }


        // Validate the total of payments for this bill
        val paymentsBeforeBillUpdate = paymentDataSource.getBillPayments(billId)
        assertEquals(
            expected = numOfPayments,
            actual = paymentsBeforeBillUpdate.size
        )

        // Updating existing bill
        billDataSource.update(bill.copy(id = billId, price = 11111))

        // Checking updated bill
        val updatedBillResult = billDataSource.getBill(billId)
        assertEquals(
            updatedBillResult?.price,
            actual = 11111
        )


        // Validate if the total of payments for this bill still the same
        val resultAfterBillUpdate = paymentDataSource.getBillPayments(billId)
        assertEquals(
            expected = numOfPayments,
            actual = resultAfterBillUpdate.size
        )
    }
}