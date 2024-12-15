package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.ui.di.commonModule
import br.com.noartcode.theprice.ui.di.commonTestModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveMonthTest: KoinTest {

    private val moveMonth:IMoveMonth by inject()

    @BeforeTest
    fun before() {
        startKoin {
            modules(commonModule())
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }


    @Test
    fun `Should return date with month 4`() {
        val result = moveMonth(
            by = 3,
            currentDate = DayMonthAndYear(day = 1, month = 1, year = 1990)
        )
        assertEquals(result.month, 4)
    }

    @Test
    fun `Should return date with month 1`() {
        val result = moveMonth(
            by = -3,
            currentDate = DayMonthAndYear(day = 1, month = 4, year = 1990)
        )
        assertEquals(result.month, 1)
    }
}