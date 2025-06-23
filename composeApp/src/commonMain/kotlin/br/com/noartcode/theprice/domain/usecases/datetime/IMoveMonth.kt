package br.com.noartcode.theprice.domain.usecases.datetime

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

interface IMoveMonth {
    operator fun invoke(by:Int, currentDate:DayMonthAndYear) : DayMonthAndYear
}

class MoveMonth : IMoveMonth {
    override fun invoke(by: Int, currentDate: DayMonthAndYear): DayMonthAndYear {
        val ld = LocalDate(year = currentDate.year,
            monthNumber = currentDate.month,
            dayOfMonth = 1
        ).plus(by, DateTimeUnit.MONTH)
        return DayMonthAndYear(
            year = ld.year,
            month = ld.monthNumber,
            day = currentDate.day
        )
    }
}