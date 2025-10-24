package br.com.noartcode.theprice.data.local.datasource.payment

import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.data.helpers.stubPayments
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentLocalDataSourceTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val billDataSource: BillLocalDataSource by inject()

    // Unit Under Test
    private val paymentDataSource: PaymentLocalDataSource by inject()


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
        database.close()
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun `Should successfully add new items into the database`() = runTest {
        val id = billDataSource.insert(bill = stubBills[0])


        assertEquals(
            expected = "7b98a36f-2694-429d-897f-cc61ca22eccb",
            actual =  paymentDataSource.insert(
                stubPayments[0].copy(
                    id = "7b98a36f-2694-429d-897f-cc61ca22eccb",
                    billId = id,
                )
            )
        )
        assertEquals(
            expected = "9d94ae70-7d60-422e-bd86-eace3b713a04",
            actual =  paymentDataSource.insert(
                stubPayments[1].copy(
                    id = "9d94ae70-7d60-422e-bd86-eace3b713a04",
                    billId = id,
                )
            )
        )
        assertEquals(
            expected = "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
            actual =  paymentDataSource.insert(
                stubPayments[2].copy(
                    id = "5dfe3b88-ea9e-4f04-9cd3-9eac88d0bce2",
                    billId = id,
                )
            )
        )
        assertEquals(expected = 3, paymentDataSource.getBillPayments(id).size)
    }


    @Test
    fun `When updating an existing bill previous bills paid shouldn't changed`() = runTest {

        // Adding payments for a bill
        val bill = stubBills[0]
        val billId = billDataSource.insert(bill)
        val numOfPayments = 3
        with(paymentDataSource) {
            repeat(numOfPayments) { i ->
                val paymentMonth = bill.billingStartDate.month + i
                insert(
                    stubPayments[0].copy(
                        billId = billId,
                        dueDate = DayMonthAndYear(day = 5, month = paymentMonth, year = 2024)
                    )
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