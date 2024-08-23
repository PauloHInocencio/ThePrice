package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char

interface IGetDateFormat {

    operator fun invoke(date: DayMonthAndYear) : String
}

class GetDateFormat : IGetDateFormat {
    override fun invoke(date: DayMonthAndYear): String {
        return LocalDate(year = date.year, monthNumber = date.month, dayOfMonth = date.day).format(
            LocalDate.Format {
                dayOfMonth()
                char('/')
                monthNumber()
                char('/')
                year()
            }
        )
    }
}