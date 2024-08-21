package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.ui.di.platformTestModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyFormatterTest : KoinTest {

    private val formatter: ICurrencyFormatter by inject()


    @BeforeTest
    fun before() {
        startKoin { modules(platformTestModule()) }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `Should return the correct format for a valid input`() {

        assertEquals(
            expected = "R$ 0,01",
            actual = formatter.format(1)
        )

        assertEquals(
            expected = "R$ 0,11",
            actual = formatter.format(11)
        )

        assertEquals(
            expected = "R$ 1,11",
            actual = formatter.format(111)
        )

    }


    @Test
    fun `Should return the correct Integer value for a valid input`() {
        assertEquals(
            expected = 1,
            actual = formatter.clenup("R$ 0,01")
        )

        assertEquals(
            expected = 11,
            actual = formatter.clenup("R$ 0,11")
        )

        assertEquals(
            expected = 111,
            actual = formatter.clenup("R$ 1,11")
        )
    }


    @Test
    fun `Should return '-1' for an invalid input`() {
        assertEquals(
            expected = -1,
            actual = formatter.clenup("R$")
        )

        assertEquals(
            expected = -1,
            actual = formatter.clenup("")
        )

        assertEquals(
            expected = -1,
            actual = formatter.clenup(" ")
        )
    }


    @Test
    fun `should return currency with correct group separator`() {
        assertEquals(
            expected = "R$ 1.111,11",
            actual = formatter.format(111111)
        )
    }

}