package br.com.noartcode.theprice.ui.presentation.splash

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.domain.usecases.user.IGetUserAccountInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val savedUser = MutableStateFlow<User?>(null)
    private val fakeUser = User(
        name = "Test", email = "test@email.com", picture = "",
        accessToken = "access", refreshToken = "refresh",
    )

    private val restartCalls = mutableListOf<Boolean>()
    private val worker = object : IOverduePaymentReminderWorker {
        override fun sweepDailyDue(restart: Boolean) { restartCalls += restart }
    }

    @BeforeTest
    fun before() = Dispatchers.setMain(testDispatcher)

    @AfterTest
    fun after() = Dispatchers.resetMain()

    private fun createViewModel() = SplashViewModel(
        getAccountInfo = IGetUserAccountInfo { savedUser },
        overdueReminderWorker = worker,
    )

    @Test
    fun `When a session exists it should navigate to home and keep the reminder schedule`() = runTest {
        savedUser.value = fakeUser
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(SplashUiState(), awaitItem())
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = true), awaitItem())
        }
        assertEquals(listOf(false), restartCalls)
    }

    @Test
    fun `When there is no session it should navigate to login and not schedule reminders`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(SplashUiState(), awaitItem())
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = false), awaitItem())
        }
        assertTrue(restartCalls.isEmpty())
    }

    @Test
    fun `When ui state is not collected it should not check the session`() = runTest {
        savedUser.value = fakeUser
        val viewModel = createViewModel()
        runCurrent()

        // Nothing collected yet: no session read, no scheduling
        assertTrue(restartCalls.isEmpty())

        viewModel.uiState.test {
            assertEquals(SplashUiState(), awaitItem())
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = true), awaitItem())
        }
        assertEquals(listOf(false), restartCalls)
    }

    @Test
    fun `When credentials are saved after the check it should not change the decision`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            skipItems(1)
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = false), awaitItem())

            // Simulates LoginUser saving credentials during a login
            savedUser.value = fakeUser
            runCurrent()
            expectNoEvents()
        }
    }
}
