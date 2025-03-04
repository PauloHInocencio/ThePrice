package br.com.noartcode.theprice.ui.mapper

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import br.com.noartcode.theprice.ui.presentation.home.model.PaymentUi
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

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentUiMapperTest : KoinTest, RobolectricTests() {

    private val testScope = TestScope()
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val database: ThePriceDatabase by inject()
    private val getTodayDate:IGetTodayDate by inject()
    private val billDataSource: BillLocalDataSource by inject()

    // Unit Under Test
    private val paymentUiMapper:UiMapper<Payment, PaymentUi?> by inject()

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
    fun `Should map a payed payment correctly`() = runTest {

        // GIVEN
        val bill = stubBills[0]
        val billID = billDataSource.insert(bill)

        //WHEN
        val uiPayment = paymentUiMapper.mapFrom(
            Payment(
                id = 1,
                billId = billID,
                dueDate = DayMonthAndYear(day = 3, month = 8, year = 2024),
                price = bill.price,
                isPayed = true
            )
        )

        //THEN
        with(uiPayment) {
            assertEquals(expected = "internet", actual = this?.billName)
            assertEquals(expected = "PAYED", this?.status?.name)
            assertEquals(expected = "Paid in 03/08/2024", actual = this?.statusDescription)
            assertEquals(expected = "R$ 120,99", this?.price)
        }
    }

    @Test
    fun `Should map a pending payment correctly`() = runTest {

        // GIVEN
        val bill = stubBills[0]
        val billID = billDataSource.insert(bill)
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 1, month = 8, year = 2024)

        //WHEN
        val uiPayment = paymentUiMapper.mapFrom(
            Payment(
                id = 1,
                billId = billID,
                dueDate = DayMonthAndYear(day = bill.billingStartDate.day, month = 8, year = 2024),
                price = bill.price,
                isPayed = false,
            )
        )

        //THEN
        with(uiPayment) {
            assertEquals(expected = "internet", actual = this?.billName)
            assertEquals(expected = "PENDING", this?.status?.name)
            assertEquals(expected = "Expires in 4 days", actual = this?.statusDescription)
            assertEquals(expected = "R$ 120,99", this?.price)
        }
    }

    @Test
    fun `Should map a overdue payment correctly`() = runTest {

        // GIVEN
        val bill = stubBills[0]
        val billID = billDataSource.insert(bill)
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 7, month = 8, year = 2024)

        //WHEN
        val uiPayment = paymentUiMapper.mapFrom(
            Payment(
                id = 1,
                billId = billID,
                dueDate = DayMonthAndYear(day = bill.billingStartDate.day, month = 8, year = 2024),
                price = bill.price,
                isPayed = false,
            )
        )

        //THEN
        with(uiPayment) {
            assertEquals(expected = "internet", actual = this?.billName)
            assertEquals(expected = "OVERDUE", this?.status?.name)
            assertEquals(expected = "2 days overdue", actual = this?.statusDescription)
            assertEquals(expected = "R$ 120,99", this?.price)
        }
    }

}