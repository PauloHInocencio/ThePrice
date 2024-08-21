package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.ui.di.commonTestModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDateFormatTest : KoinTest {

    private val getDateFormat : IGetDateFormat by inject()

    @BeforeTest
    fun before() {
        startKoin { modules(commonTestModule()) }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `Should return the correct format of a date`() {

        assertEquals(
            expected = "01/08/1990",
            actual = getDateFormat(day = 1, month = 8, year = 1990)
        )

    }

}