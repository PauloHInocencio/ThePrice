package br.com.noartcode.theprice.domain.usecases

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.char

interface IGetDateFormat {

    operator fun invoke(day:Int, month:Int, year:Int) : String
}

class GetDateFormat : IGetDateFormat {
    override fun invoke(day: Int, month: Int, year: Int): String {
        return LocalDate(year = year, monthNumber = month, dayOfMonth = day).format(
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