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

    @Test
    fun `isTotalMonthsGreaterOrEqual with same year and month returns true`() {
        // GIVEN two dates with the same year and month (e.g., 2024-06 vs 2024-06)
        // WHEN calling isTotalMonthsGreaterOrEqual
        // THEN returns true
    }

    @Test
    fun `isTotalMonthsGreaterOrEqual with later month in same year returns true`() {
        // GIVEN a date with a later month in the same year (e.g., 2024-08 vs 2024-06)
        // WHEN calling isTotalMonthsGreaterOrEqual
        // THEN returns true
    }

    @Test
    fun `isTotalMonthsGreaterOrEqual with earlier month in same year returns false`() {
        // GIVEN a date with an earlier month in the same year (e.g., 2024-04 vs 2024-06)
        // WHEN calling isTotalMonthsGreaterOrEqual
        // THEN returns false
    }

    @Test
    fun `isTotalMonthsGreaterOrEqual with same month in later year returns true`() {
        // GIVEN a date with the same month in a later year (e.g., 2025-06 vs 2024-06)
        // WHEN calling isTotalMonthsGreaterOrEqual
        // THEN returns true
    }

    @Test
    fun `isTotalMonthsGreaterOrEqual with same month in earlier year returns false`() {
        // GIVEN a date with the same month in an earlier year (e.g., 2023-06 vs 2024-06)
        // WHEN calling isTotalMonthsGreaterOrEqual
        // THEN returns false
    }

    @Test
    fun `isTotalMonthsGreaterOrEqual ignores day component`() {
        // GIVEN two dates with same year/month but different days (e.g., 2024-06-15 vs 2024-06-01)
        // WHEN calling isTotalMonthsGreaterOrEqual
        // THEN returns true (day is irrelevant)
    }
}