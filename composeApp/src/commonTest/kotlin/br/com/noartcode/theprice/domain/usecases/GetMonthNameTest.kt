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

class GetMonthNameTest : KoinTest {

    private val getMonthName:IGetMonthName by inject()


    @BeforeTest
    fun before() {
        startKoin { modules(platformTestModule()) }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `Should return correct month's name`() {

        assertEquals(
            expected = "Janeiro",
            actual = getMonthName(month = 1)
        )

        assertEquals(
            expected = "Dezembro",
            actual = getMonthName(month = 12)
        )
    }



}