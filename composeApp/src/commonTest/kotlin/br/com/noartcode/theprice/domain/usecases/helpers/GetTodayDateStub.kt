package br.com.noartcode.theprice.domain.usecases.helpers

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate

class GetTodayDateStub(
    var date: DayMonthAndYear = DayMonthAndYear(day = 1, month = 8, year = 1990)
) : IGetTodayDate {
    override fun invoke(): DayMonthAndYear {
        return date
    }
}