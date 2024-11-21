package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSourceImp
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

@OptIn(ExperimentalCoroutinesApi::class)
class UpdatePaymentTest : KoinTest, RobolectricTests() {
    private val database: ThePriceDatabase by inject()
    private val formatter: ICurrencyFormatter by inject()
    private val paymentDataSource: PaymentLocalDataSource by lazy { PaymentLocalDataSourceImp(database) }
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val getTodayDate: IGetTodayDate = GetTodayDateStub()
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter, getTodayDate) }

    // Unit under test
    private val updatePayment: IUpdatePayment by lazy {
        UpdatePayment(
            datasource = paymentDataSource,
            currencyFormatter = formatter,
            dispatcher = UnconfinedTestDispatcher()
        )
    }

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
    fun `Payment should be update`() = runTest {

    }


}
