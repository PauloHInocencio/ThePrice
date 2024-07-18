package br.com.noartcode.theprice.domain.usecases

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

interface IGetTodayDate {
    operator fun invoke() : String
}

class GetTodayDate : IGetTodayDate {
    override fun invoke(): String {
        val timeZone = TimeZone.currentSystemDefault()
        return Clock.System.todayIn(timeZone).toString()
    }

}