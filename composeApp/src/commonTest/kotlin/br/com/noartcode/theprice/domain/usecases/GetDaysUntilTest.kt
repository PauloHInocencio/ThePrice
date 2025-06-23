package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.datetime.IGetDaysUntil
import br.com.noartcode.theprice.ui.di.commonModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDaysUntilTest : KoinTest {

    private val getDaysUntil: IGetDaysUntil by inject()


    @BeforeTest
    fun before() {
        startKoin {
            modules(
                commonModule(),
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `Should return 5 days as the answer`() {

        assertEquals(
            expected = 5,
            actual = getDaysUntil(
                startDate = DayMonthAndYear(day = 22, month = 8, year = 2024),
                endDate = DayMonthAndYear(day = 27, month = 8, year = 2024),
            )
        )

        assertEquals(
            expected = -5,
            actual = getDaysUntil(
                startDate = DayMonthAndYear(day = 27, month = 8, year = 2024),
                endDate = DayMonthAndYear(day = 22, month = 8, year = 2024)
            )
        )

        assertEquals(
            expected = 0,
            actual = getDaysUntil(
                startDate = DayMonthAndYear(day = 22, month = 8, year = 2024),
                endDate = DayMonthAndYear(day = 22, month = 8, year = 2024)
            )
        )

    }

}