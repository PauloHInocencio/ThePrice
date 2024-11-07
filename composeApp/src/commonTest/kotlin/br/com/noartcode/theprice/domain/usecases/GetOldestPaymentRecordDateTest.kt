package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSourceImp
import br.com.noartcode.theprice.data.localdatasource.helpers.stubBills
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
class GetOldestPaymentRecordDateTest:KoinTest, RobolectricTests() {


    private val database: ThePriceDatabase by inject()
    private val epochFormatter: IEpochMillisecondsFormatter by inject()
    private val billDataSource: BillLocalDataSource by lazy { BillLocalDataSourceImp(database, epochFormatter) }
    private val getFirstPaymentDate:IGetOldestPaymentRecordDate by lazy {
        GetOldestPaymentRecordDate(localDataSource = billDataSource, epochFormatter = epochFormatter)
    }
    @BeforeTest
    fun before() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        startKoin {
            modules(platformTestModule(),  commonTestModule())
        }
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        database.close()
    }

    @Test
    fun `Should return the oldest bill record from the database`() = runTest {

        // GIVEN
        billDataSource.apply {
            insert(stubBills[0])
            insert(stubBills[1])
            insert(stubBills[2])
        }


        //WHEN
        getFirstPaymentDate().test {
            with(awaitItem()) {
                assertEquals(7, this?.month ?: 1)
            }
        }
    }

}