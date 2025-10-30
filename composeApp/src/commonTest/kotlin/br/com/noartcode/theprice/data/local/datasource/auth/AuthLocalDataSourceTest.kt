package br.com.noartcode.theprice.data.local.datasource.auth

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AuthLocalDataSourceTest: KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()

    private val dataSource: AuthLocalDataSource by inject()

    private val user = User(
        accessToken = "test_access_token",
        refreshToken = "test_refresh_token",
        name = "Test Name",
        email = "test@email.com",
        picture = "photo_url",
    )

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                platformTestModule(),
                commonModule(),
                dispatcherTestModule(),
                commonTestModule()
            )
        }
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        stopKoin()
        // Clean up the actual file that was created
        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }

    @Test
    fun `Should successfully save user credentials into preferences`() = runTest {

        dataSource.saveCredentials(user = user)

        dataSource.getUser().test {

            val user = awaitItem()
            assertNotNull(user)
            assertEquals(expected = "Test Name", user.name)
            assertEquals(expected = "test@email.com", user.email)
            assertEquals(expected = "photo_url", user.picture)
            assertEquals(expected = "test_refresh_token", user.refreshToken)
            assertEquals(expected = "test_access_token", user.accessToken)

            ensureAllEventsConsumed()
        }

    }

    @Test
    fun `Should successfully cleanup user credentials from preferences`() = runTest {

        dataSource.saveCredentials(user = user)

        dataSource.getUser().test {

            // Confirm that user exist after cleanup
            var user = awaitItem()
            assertNotNull(user)
            ensureAllEventsConsumed()

            // After cleanup user reference should be null
            dataSource.clean()
            user = awaitItem()
            assertNull(user)
            ensureAllEventsConsumed()
        }

    }

}