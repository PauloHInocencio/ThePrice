package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.datetime.toLocalDate

interface IGetDateMonthAndYear {
    operator fun invoke(date:String) : DayMonthAndYear
}

class GetDateMonthAndYear : IGetDateMonthAndYear {

    override fun invoke(date: String): DayMonthAndYear {
        val ld = date.toLocalDate()
        return DayMonthAndYear(
            day = ld.dayOfMonth,
            month = ld.monthNumber - 1,
            year = ld.year
        )
    }

}