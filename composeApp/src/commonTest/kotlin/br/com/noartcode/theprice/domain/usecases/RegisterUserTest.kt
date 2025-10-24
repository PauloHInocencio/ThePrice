package br.com.noartcode.theprice.domain.usecases

import app.cash.turbine.test
import br.com.noartcode.theprice.data.local.preferences.cleanupDataStoreFile
import br.com.noartcode.theprice.domain.usecases.user.IRegisterUser
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterUserTest : KoinTest, RobolectricTests() {

    private val testDispatcher: CoroutineDispatcher by inject()

    // Unit Under Test
    private val registerUser: IRegisterUser by inject()


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

    @Test
    fun `Should Register User Successfully`() = runTest {
        val userName = "Test Silva"
        val email = "test.silva@email.com"
        val password = "123456"

        registerUser(name = userName, email = email, password = password).test {

            assertTrue(awaitItem() is Resource.Loading)

            val result = awaitItem()
            assertTrue(result is Resource.Success)

            val user = result.data
            assertTrue(user.accessToken.isNotBlank())
            assertTrue(user.refreshToken.isNotBlank())
            assertEquals(userName, user.name)
            assertEquals(email, user.email)

            awaitComplete()
        }
    }
}