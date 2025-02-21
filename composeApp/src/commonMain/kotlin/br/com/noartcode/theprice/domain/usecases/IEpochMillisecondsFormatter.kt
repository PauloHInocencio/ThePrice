package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toLocalDate
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
        //val ld = LocalDate(dayOfMonth = date.day, monthNumber = date.month, year = date.year)
        return date.toLocalDate().atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }

    override fun to(epoch: Long): DayMonthAndYear {
        return Instant
            .fromEpochMilliseconds(epoch)
            .toLocalDateTime(TimeZone.UTC)
            .date
            .toDayMonthAndYear()
        //return DayMonthAndYear(day = ld.dayOfMonth, month = ld.monthNumber, year = ld.year)
    }
}