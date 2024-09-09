package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.periodUntil


interface IGetDaysUntil {
    operator fun invoke(
        startDate:DayMonthAndYear,
        endDate:DayMonthAndYear
    ) : Int
}

class GetDaysUntil : IGetDaysUntil {
    override fun invoke(
        startDate:DayMonthAndYear,
        endDate:DayMonthAndYear
    ): Int {
        val s = LocalDate(year = startDate.year, monthNumber = startDate.month, dayOfMonth = startDate.day)
        val e =  LocalDate(year = endDate.year, monthNumber = endDate.month, dayOfMonth = endDate.day)
        return s.daysUntil(e)
    }
}

