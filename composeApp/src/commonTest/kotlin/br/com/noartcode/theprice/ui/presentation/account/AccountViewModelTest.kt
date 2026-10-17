package br.com.noartcode.theprice.ui.presentation.account

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.datasource.settings.SettingsLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.domain.model.AppCurrency
import br.com.noartcode.theprice.domain.model.AppLanguage
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.platform.locale.ILocaleManager
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import br.com.noartcode.theprice.ui.di.viewModelsModule
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
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class AccountViewModelTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val authLocalDataSource: AuthLocalDataSource by inject()
    private val settingsLocalDataSource: SettingsLocalDataSource by inject()
    private val localeManager: ILocaleManager by inject()

    // Unit Under Test
    private val viewModel: AccountViewModel by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule(),
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
    fun `When a different language is selected it should save it and ask for a restart`() = runTest {
        viewModel.uiState.test {
            assertEquals(AccountUiState(), awaitItem())

            // WHEN
            viewModel.onEvent(AccountEvent.LanguageSelected(AppLanguage.PORTUGUESE))

            // THEN
            with(awaitItem()) {
                assertEquals(AppLanguage.PORTUGUESE, selectedLanguage)
                assertTrue(showRestartRequiredMessage)
            }
            verify(VerifyMode.exactly(1)) { localeManager.apply(AppLanguage.PORTUGUESE) }

            // WHEN
            viewModel.onEvent(AccountEvent.RestartMessageDismissed)

            // THEN
            assertFalse(awaitItem().showRestartRequiredMessage)
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `When a different currency is selected it should save it and ask for a restart`() = runTest {
        viewModel.uiState.test {
            assertEquals(AccountUiState(), awaitItem())

            // WHEN
            viewModel.onEvent(AccountEvent.CurrencySelected(AppCurrency.BRL))

            // THEN
            with(awaitItem()) {
                assertEquals(AppCurrency.BRL, selectedCurrency)
                assertTrue(showRestartRequiredMessage)
            }
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `When logout succeeds it should reset the state but keep the selected settings`() = runTest {
        // GIVEN
        settingsLocalDataSource.saveLanguage(AppLanguage.PORTUGUESE.code)
        settingsLocalDataSource.saveCurrency(AppCurrency.BRL.code)
        authLocalDataSource.saveCredentials(User(
            name = "Test Silva", email = "test@email.com", picture = "",
            accessToken = "fake_access_token", refreshToken = "fake_refresh_token",
        ))
        authLocalDataSource.saveDeviceID(deviceID = Uuid.random().toString())

        viewModel.uiState.test {
            skipItems(1)
            runCurrent()
            expectMostRecentItem() // user and settings loaded

            // WHEN
            viewModel.onEvent(AccountEvent.LogoutConfirmed)

            // THEN
            assertTrue(awaitItem().loading)
            skipItems(1) // user cleared
            with(awaitItem()) {
                assertTrue(loggedOut)
                assertEquals(AppLanguage.PORTUGUESE, selectedLanguage)
                assertEquals(AppCurrency.BRL, selectedCurrency)
            }
        }
    }
}
