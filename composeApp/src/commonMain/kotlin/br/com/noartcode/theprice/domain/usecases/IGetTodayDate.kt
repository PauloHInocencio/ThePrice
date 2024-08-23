package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

interface IGetTodayDate {
    operator fun invoke() : DayMonthAndYear
}

class GetTodayDate : IGetTodayDate {
    override fun invoke(): DayMonthAndYear {
        val timeZone = TimeZone.currentSystemDefault()
        val date = Clock.System.todayIn(timeZone)
        return DayMonthAndYear(day = date.dayOfMonth, month = date.monthNumber, year = date.year)
    }
}