package br.com.noartcode.theprice.ui.presentation.login

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.loginUserMockModule
import br.com.noartcode.theprice.ui.di.overdueReminderWorkerMockModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import br.com.noartcode.theprice.util.Resource
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.getValue
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val worker: IOverduePaymentReminderWorker by inject()

    // Unit Under Test
    private val viewModel: LoginViewModel by inject()

    // Each value sent here is emitted by the mocked LoginUser, so the test controls every step
    private val loginSteps = Channel<Resource<Unit>>()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
                overdueReminderWorkerMockModule(),
                viewModelsModule()
            )
        }
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }

    @Test
    fun `When login is in progress it should not be authenticated until Success`() = runTest {
        loadKoinModules(loginUserMockModule(loginSteps.receiveAsFlow()))

        viewModel.uiState.test {
            assertEquals(LoginUiState(), awaitItem())

            viewModel.onEvent(LoginEvent.LoginInWithGoogle)
            loginSteps.send(Resource.Loading)
            with(awaitItem()) {
                assertTrue(isAuthenticatingWithGoogle)
                assertFalse(isAuthenticated)
            }
            verify(VerifyMode.not) { worker.sweepDailyDue(any()) }

            loginSteps.send(Resource.Success(Unit))
            with(awaitItem()) {
                assertTrue(isAuthenticated)
                // Spinner must stay up until the screen navigates away
                assertTrue(isAuthenticatingWithGoogle)
            }
            verify(VerifyMode.exactly(1)) { worker.sweepDailyDue(restart = true) }
        }
    }

    @Test
    fun `When login fails it should show the error and not schedule reminders`() = runTest {
        loadKoinModules(loginUserMockModule(loginSteps.receiveAsFlow()))

        viewModel.uiState.test {
            skipItems(1)

            viewModel.onEvent(LoginEvent.LoginInWithGoogle)
            loginSteps.send(Resource.Loading)
            skipItems(1)

            loginSteps.send(Resource.Error(message = "Error while making http request"))
            with(awaitItem()) {
                assertEquals("Error while making http request", errorMessage)
                assertFalse(isAuthenticated)
            }
            verify(VerifyMode.not) { worker.sweepDailyDue(any()) }
        }
    }

    @Test
    fun `When login flow throws it should show the error instead of crashing`() = runTest {
        loadKoinModules(
            loginUserMockModule(
                flow {
                    emit(Resource.Loading)
                    throw IllegalStateException("database is locked")
                }
            )
        )

        viewModel.uiState.test {
            skipItems(1)

            viewModel.onEvent(LoginEvent.LoginInWithGoogle)
            runCurrent()

            // Loading and Error happen in the same tick; StateFlow may conflate them, so check the latest
            with(expectMostRecentItem()) {
                assertEquals("database is locked", errorMessage)
                assertFalse(isAuthenticated)
            }
        }
    }
}
