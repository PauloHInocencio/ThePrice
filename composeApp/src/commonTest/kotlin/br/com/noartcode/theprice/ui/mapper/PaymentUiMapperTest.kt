package br.com.noartcode.theprice.ui.mapper

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import br.com.noartcode.theprice.domain.usecases.IGetDaysUntil
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import br.com.noartcode.theprice.domain.usecases.helpers.GetTodayDateStub
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val database: ThePriceDatabase by inject()
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val getTodayDate:IGetTodayDate = GetTodayDateStub()
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter, getTodayDate) }
    private val formatter:ICurrencyFormatter by inject()
    private val dateFormat:IGetDateFormat by inject()
    private val getDaysUntil:IGetDaysUntil by inject()

    private val paymentUiMapper:PaymentDomainToUiMapper by lazy {
        PaymentDomainToUiMapper(
            dataSource = billDataSource,
            formatter = formatter,
            getTodayDate = getTodayDate,
            dateFormat = dateFormat,
            getDaysUntil = getDaysUntil,
        )
    }

    @BeforeTest
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        startKoin {
            modules(
                commonTestModule(),
                platformTestModule(),
            )
        }
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        database.close()
    }


    @Test
    fun `Should map a payed payment correctly`() = runTest {

        // GIVEN
        val bill = stubBills[0]
        billDataSource.insert(
            name = bill.name,
            description = bill.description,
            price = bill.price,
            type = bill.type,
            status = bill.status,
            billingStartDate = bill.billingStartDate,
        )

        //WHEN
        val uiPayment = paymentUiMapper.mapFrom(
            Payment(
                id = 1,
                billId = 1,
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
        billDataSource.insert(
            name = bill.name,
            description = bill.description,
            price = bill.price,
            type = bill.type,
            status = bill.status,
            billingStartDate = bill.billingStartDate,
        )
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 1, month = 8, year = 2024)

        //WHEN
        val uiPayment = paymentUiMapper.mapFrom(
            Payment(
                id = 1,
                billId = 1,
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
        billDataSource.insert(
            name = bill.name,
            description = bill.description,
            price = bill.price,
            type = bill.type,
            status = bill.status,
            billingStartDate = bill.billingStartDate
        )
        (getTodayDate as GetTodayDateStub).date = DayMonthAndYear(day = 7, month = 8, year = 2024)

        //WHEN
        val uiPayment = paymentUiMapper.mapFrom(
            Payment(
                id = 1,
                billId = 1,
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