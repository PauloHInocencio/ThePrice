package br.com.noartcode.theprice.ui.presentation.login

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.domain.usecases.user.ILoginUser
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Each value sent here is emitted by the fake LoginUser, so the test controls every step
    private val loginSteps = Channel<Resource<Unit>>()
    private var loginFlow: Flow<Resource<Unit>> = loginSteps.receiveAsFlow()

    private val worker = object : IOverduePaymentReminderWorker {
        var calls = 0
        var lastRestart: Boolean? = null
        override fun sweepDailyDue(restart: Boolean) { calls++; lastRestart = restart }
    }

    @BeforeTest
    fun before() = Dispatchers.setMain(testDispatcher)

    @AfterTest
    fun after() = Dispatchers.resetMain()

    private fun createViewModel() = LoginViewModel(
        loginInUser = object : ILoginUser {
            override fun invoke(): Flow<Resource<Unit>> = loginFlow
        },
        overdueReminderWorker = worker,
    )

    @Test
    fun `When login is in progress it should not be authenticated until Success`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(LoginUiState(), awaitItem())

            viewModel.onEvent(LoginEvent.LoginInWithGoogle)
            loginSteps.send(Resource.Loading)
            with(awaitItem()) {
                assertTrue(isAuthenticatingWithGoogle)
                assertFalse(isAuthenticated)
            }
            assertEquals(0, worker.calls)

            loginSteps.send(Resource.Success(Unit))
            with(awaitItem()) {
                assertTrue(isAuthenticated)
                // Spinner must stay up until the screen navigates away
                assertTrue(isAuthenticatingWithGoogle)
            }
            assertEquals(1, worker.calls)
            assertEquals(true, worker.lastRestart)
        }
    }

    @Test
    fun `When login fails it should show the error and not schedule reminders`() = runTest {
        val viewModel = createViewModel()

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
            assertEquals(0, worker.calls)
        }
    }

    @Test
    fun `When login flow throws it should show the error instead of crashing`() = runTest {
        loginFlow = flow {
            emit(Resource.Loading)
            throw IllegalStateException("database is locked")
        }
        val viewModel = createViewModel()

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
