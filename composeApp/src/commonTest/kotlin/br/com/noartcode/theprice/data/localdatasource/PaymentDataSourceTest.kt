package br.com.noartcode.theprice.data.localdatasource

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
import br.com.noartcode.theprice.data.localdatasource.helpers.stubPayments
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
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentDataSourceTest : KoinTest, RobolectricTests() {

    private val database:ThePrinceDatabase by inject()
    private val dateFormat:IGetDateFormat by inject()
    private val paymentDataSource: PaymentLocalDataSource by lazy { PaymentLocalDataSourceImp(database, dateFormat) }
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database) }


    @BeforeTest
    fun before() {
        startKoin { modules(platformTestModule(), commonTestModule()) }
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        database.close()
    }

    @Test
    fun `Should successfully add new items into the database`() = runTest {

        billDataSource.insert(stubBills[0])

        assertEquals(expected = 1, paymentDataSource.insert(billID = 1L, day = 5, month = 1, year = 2024))
        assertEquals(expected = 2, paymentDataSource.insert(billID = 1L, day = 5, month = 2, year = 2024))
        assertEquals(expected = 3, paymentDataSource.insert(billID = 1L, day = 5, month = 3, year = 2024))
        assertEquals(expected = 3, paymentDataSource.getBillPayments(1).size)
    }
}