package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.MonthAndYear
import kotlinx.datetime.toLocalDate

interface IGetDateMonthAndYear {
    operator fun invoke(date:String) : MonthAndYear
}

class GetDateMonthAndYear : IGetDateMonthAndYear {

    override fun invoke(date: String): MonthAndYear {
        val ld = date.toLocalDate()
        return MonthAndYear(
            month = ld.monthNumber - 1,
            year = ld.year
        )
    }

}