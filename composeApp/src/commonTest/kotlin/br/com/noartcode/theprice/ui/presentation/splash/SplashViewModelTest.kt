package br.com.noartcode.theprice.ui.presentation.splash

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.settings.AppSettingsCache
import br.com.noartcode.theprice.data.local.datasource.settings.SettingsLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.data.local.workers.IOverduePaymentReminderWorker
import br.com.noartcode.theprice.domain.model.AppCurrency
import br.com.noartcode.theprice.domain.model.AppLanguage
import br.com.noartcode.theprice.domain.model.AppSettings
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.platform.locale.ILocaleManager
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.overdueReminderWorkerMockModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
import dev.mokkery.matcher.any
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val authLocalDataSource: AuthLocalDataSource by inject()
    private val worker: IOverduePaymentReminderWorker by inject()
    private val settingsLocalDataSource: SettingsLocalDataSource by inject()
    private val appSettingsCache: AppSettingsCache by inject()
    private val localeManager: ILocaleManager by inject()

    // Unit Under Test
    private val viewModel: SplashViewModel by inject()

    private val fakeUser = User(
        name = "Test", email = "test@email.com", picture = "",
        accessToken = "access", refreshToken = "refresh",
    )

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
    fun `When a session exists it should navigate to home and keep the reminder schedule`() = runTest {
        authLocalDataSource.saveCredentials(fakeUser)

        viewModel.uiState.test {
            assertEquals(SplashUiState(), awaitItem())
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = true), awaitItem())
        }
        verify(VerifyMode.exactly(1)) { worker.sweepDailyDue(restart = false) }
    }

    @Test
    fun `When there is no session it should navigate to login and not schedule reminders`() = runTest {
        viewModel.uiState.test {
            assertEquals(SplashUiState(), awaitItem())
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = false), awaitItem())
        }
        verify(VerifyMode.not) { worker.sweepDailyDue(any()) }
    }

    @Test
    fun `When credentials are saved after the check it should not change the decision`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            assertEquals(SplashUiState(isCheckingSession = false, isAuthenticated = false), awaitItem())

            // Simulates LoginUser saving credentials during a login
            authLocalDataSource.saveCredentials(fakeUser)
            runCurrent()
            expectNoEvents()
        }
    }

    @Test
    fun `When the session check finishes it should have applied the stored settings`() = runTest {
        // GIVEN
        settingsLocalDataSource.saveLanguage(AppLanguage.PORTUGUESE.code)
        settingsLocalDataSource.saveCurrency(AppCurrency.BRL.code)

        viewModel.uiState.test {
            skipItems(1)

            // WHEN
            assertFalse(awaitItem().isCheckingSession)

            // THEN: resolved before any localized screen is shown
            assertEquals(AppSettings(AppLanguage.PORTUGUESE, AppCurrency.BRL), appSettingsCache.settings)
        }
        verify(VerifyMode.exactly(1)) { localeManager.apply(AppLanguage.PORTUGUESE) }
    }
}
