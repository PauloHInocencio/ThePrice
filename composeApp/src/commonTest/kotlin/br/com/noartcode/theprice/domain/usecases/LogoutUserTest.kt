package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.helpers.stubSyncEvents
import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.remote.workers.ISyncEventsWorker
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.domain.usecases.user.ILogoutUser
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
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class LogoutUserTest: KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val database: ThePriceDatabase by inject()
    private val authLocalDataSource: AuthLocalDataSource by inject()
    private val user = User(
        name = "Test Silva",
        email = "test@email.com",
        picture = "",
        accessToken = "fake_access_token",
        refreshToken = "fake_refresh_token"
    )
    private val billsRepository:BillsRepository by inject()
    private val paymentsRepository:PaymentsRepository by inject()
    private val eventSyncQueue: EventSyncQueue by inject()

    // Unit Under Test
    private val logoutUser: ILogoutUser by inject()


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
    fun `After logout users credentials should be null`() = runTest {

        // GIVEN
        authLocalDataSource.saveCredentials(user)
        authLocalDataSource.saveDeviceID(deviceID = Uuid.random().toString())

        // WHEN
        logoutUser().test {

            // THEN
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()

            assertTrue(authLocalDataSource.getUser().first() == null)
            assertTrue(authLocalDataSource.getDeviceID().first() == null )

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `After logout user data should be null`() = runTest{
        // GIVEN
        authLocalDataSource.saveCredentials(user)
        authLocalDataSource.saveDeviceID(deviceID = Uuid.random().toString())
        repeat(3) { i -> eventSyncQueue.enqueue(stubSyncEvents[i]) }
        billsRepository.fetchAllBills()
        paymentsRepository.fetchAllPayments()
        assertTrue(billsRepository.getAllBills().first().size == 3)
        assertTrue(paymentsRepository.getAllPayments().first().size == 9)
        assertTrue(eventSyncQueue.size() == 3)

        // WHEN
        logoutUser().test {

            // THEN
            assertTrue(awaitItem() is Resource.Loading)
            assertTrue(awaitItem() is Resource.Success)
            awaitComplete()

            assertTrue(billsRepository.getAllBills().first().isEmpty())
            assertTrue(paymentsRepository.getAllPayments().first().isEmpty())
            assertTrue(eventSyncQueue.size() == 0)
        }
    }



}