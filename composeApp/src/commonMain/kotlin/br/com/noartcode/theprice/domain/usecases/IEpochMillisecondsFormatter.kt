package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

interface IEpochMillisecondsFormatter {
    fun from(date:DayMonthAndYear) : Long
    fun to(epoch:Long) : DayMonthAndYear
}

class EpochMillisecondsFormatter: IEpochMillisecondsFormatter {
    override fun from(date: DayMonthAndYear): Long {
        val ld = LocalDate(dayOfMonth = date.day, monthNumber = date.month, year = date.year)
        return ld.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    override fun to(epoch: Long): DayMonthAndYear {
        val ld = Instant
            .fromEpochMilliseconds(epoch)
            .toLocalDateTime(TimeZone.UTC)
        return DayMonthAndYear(day = ld.dayOfMonth, month = ld.monthNumber, year = ld.year)
    }
}