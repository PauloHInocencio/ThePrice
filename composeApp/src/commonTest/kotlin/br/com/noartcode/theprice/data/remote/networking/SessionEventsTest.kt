package br.com.noartcode.theprice.data.remote.networking

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock.EXPIRED_REFRESH_TOKEN
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock.EXPIRED_TOKEN
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock.REFRESH_SERVER_ERROR_TOKEN
import br.com.noartcode.theprice.data.remote.networking.ThePriceApiMock.RENEWED_TOKEN
import br.com.noartcode.theprice.domain.model.User
import br.com.noartcode.theprice.ui.di.RobolectricTests
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import br.com.noartcode.theprice.ui.di.currentTestFileName
import br.com.noartcode.theprice.ui.di.dispatcherTestModule
import br.com.noartcode.theprice.ui.di.platformTestModule
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class SessionEventsTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()
    private val authLocalDataSource: AuthLocalDataSource by inject()
    private val client: HttpClient by inject()

    // Unit Under Test
    private val sessionEvents: SessionEvents by inject()

    @BeforeTest
    fun before() {
        startKoin {
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
        Dispatchers.resetMain()
        stopKoin()
        currentTestFileName?.let { cleanupDataStoreFile(it) }
    }

    private suspend fun saveSession(refreshToken: String) {
        authLocalDataSource.saveCredentials(
            User(
                name = "Test Silva", email = "test@email.com", picture = "",
                accessToken = EXPIRED_TOKEN, refreshToken = refreshToken,
            )
        )
        authLocalDataSource.saveDeviceID(deviceID = Uuid.random().toString())
    }

    @Test
    fun `When the refresh token is rejected it should notify that the session expired`() = runTest {
        // GIVEN
        saveSession(refreshToken = EXPIRED_REFRESH_TOKEN)

        sessionEvents.expired.test {
            // WHEN
            client.get("bills")

            // THEN
            awaitItem()
        }
    }

    @Test
    fun `When the token is renewed it should save it and not notify that the session expired`() = runTest {
        // GIVEN
        saveSession(refreshToken = "valid_refresh_token")

        sessionEvents.expired.test {
            // WHEN
            val response = client.get("bills")

            // THEN
            assertEquals(HttpStatusCode.OK, response.status)
            expectNoEvents()
        }
        assertEquals(RENEWED_TOKEN, authLocalDataSource.getAccessToken().first())
    }

    @Test
    fun `When the refresh fails with a server error it should not notify that the session expired`() = runTest {
        // GIVEN
        saveSession(refreshToken = REFRESH_SERVER_ERROR_TOKEN)

        sessionEvents.expired.test {
            // WHEN
            client.get("bills")

            // THEN
            expectNoEvents()
        }
    }
}
