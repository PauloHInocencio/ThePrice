package domain.usecases

import domain.model.MonthAndYear
import kotlinx.datetime.toLocalDate

interface IGetDateMonthAndYear {
    operator fun invoke(date:String) : MonthAndYear
}

class GetDateMonthAndYear : IGetDateMonthAndYear {

    override fun invoke(date: String): MonthAndYear {
        val ld = date.toLocalDate()
        return MonthAndYear(month = ld.monthNumber, year = ld.year)
    }

}