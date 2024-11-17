package br.com.noartcode.theprice.domain.model

import kotlin.test.Test
import kotlin.test.assertTrue

class DayMonthAndYearTest {

    @Test
    fun `Validate date equality`() {
        val date1 = DayMonthAndYear(day = 1, month = 8, year = 1990)
        val date2 = DayMonthAndYear(day = 1, month = 8, year = 1990)
        val result = date1 == date2
        assertTrue(result)
    }


    @Test
    fun `Validate date by year`() {
        val date1 = DayMonthAndYear(day = 1, month = 8, year = 1991)
        val date2 = DayMonthAndYear(day = 1, month = 8, year = 1990)
        val result = date1 > date2
        assertTrue(result)
    }

    @Test
    fun `Validate date by month`() {
        val date1 = DayMonthAndYear(day = 1, month = 7, year = 1990)
        val date2 = DayMonthAndYear(day = 1, month = 8, year = 1990)
        val result = date1 < date2
        assertTrue(result)
    }


    @Test
    fun `Validate date by day`() {
        val date1 = DayMonthAndYear(day = 1, month = 8, year = 1990)
        val date2 = DayMonthAndYear(day = 18, month = 8, year = 1990)
        val result = date1 < date2
        assertTrue(result)
    }
}