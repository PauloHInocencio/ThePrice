package br.com.noartcode.theprice.domain.usecases.datetime

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.until

interface IGetDaysInMonth {
    operator fun invoke(year: Int, month:Int): Int
}

class GetDaysInMonth : IGetDaysInMonth {
    override fun invoke(year: Int, month: Int): Int {
        val start = LocalDate(dayOfMonth = 1, monthNumber = month, year = year)
        val end = start.plus(1, DateTimeUnit.MONTH)
        return start.until(end, DateTimeUnit.DAY)
    }
}