package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock
import br.com.noartcode.theprice.domain.usecases.user.ILoginUser
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.util.Resource
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
import kotlin.getValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginUserTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()

    private val database: ThePriceDatabase by inject()
    private val authLocalDataSource: AuthLocalDataSource by inject()
    private val billLocalDS: BillLocalDataSource by inject()
    private val paymentLocalDS: PaymentLocalDataSource by inject()

    // Unit Under Test
    private val loginUser: ILoginUser by inject()

    @BeforeTest
    fun before() {
        startKoin{
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

        // Clean up file that was created for shared preferences
        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }


    @Test
    fun `User Should Login Successfully`() = runTest {

        loginUser().test {
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()
        }
    }

    @Test
    fun `User Credentials Should be retrieved Successfully After Login`() = runTest {

        loginUser().test {
            skipItems(2)
            awaitComplete()

            val user = authLocalDataSource.getUser().first()

            assertTrue( user != null)
            assertTrue(user.accessToken.isNotBlank())
            assertTrue(user.refreshToken.isNotBlank())
            assertEquals(ThePriceApiMock.MOCK_EMAIL, user.email)
            assertEquals(ThePriceApiMock.MOCK_NAME, user.name)

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `User Data Should be retrieved Successfully After Login`() =  runTest {
        loginUser().test {
            skipItems(2)
            awaitComplete()

            assertEquals(expected = 3, billLocalDS.getAllBills().first().size)
            assertEquals(expected = 9, paymentLocalDS.getAllPayments().first().size)

            ensureAllEventsConsumed()

        }
    }

}