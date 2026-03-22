package br.com.noartcode.theprice.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

data class DayMonthAndYear(
    val day:Int,
    val month: Int,
    val year: Int
) : Comparable<DayMonthAndYear>{

    companion object {
        // TODO: MAKE this an invalid date
        val EMPTY = DayMonthAndYear(day = 1, month = 1, year = 1900)
    }

    override fun compareTo(other: DayMonthAndYear): Int {
        return compareValuesBy(this, other,
            DayMonthAndYear::year,
            DayMonthAndYear::month,
            DayMonthAndYear::day
        )
    }

    override fun toString(): String = StringBuilder().apply {
        append(if(day < 10) "0$day" else day)
        append("/")
        append(if (month < 10) "0$month" else month)
        append("/")
        append(year)
    }.toString()

}

fun DayMonthAndYear.toLocalDate() : LocalDate {
    val (day, month, year) = this.toString().split('/')
    return LocalDate.parse("$year-$month-$day")
}

fun LocalDate.toDayMonthAndYear() =
    DayMonthAndYear(day = this.dayOfMonth, month = this.monthNumber, year = this.year)

fun DayMonthAndYear.toEpochMilliseconds() =
    this.toLocalDate().atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

fun Long.toDayMonthAndYear() =
    Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
        .toDayMonthAndYear()

fun String.toDayMonthAndYear() =
    Instant
        .parse(this)
        .toLocalDateTime(TimeZone.UTC)
        .date
        .toDayMonthAndYear()

fun DayMonthAndYear.isValid() : Boolean {
    try {
        this.toLocalDate()
        return true
    } catch (e: Throwable) {
        e.printStackTrace()
        return false
    }
}

fun DayMonthAndYear.isTotalMonthsGreaterOrEqual(
    other: DayMonthAndYear
): Boolean {
    val totalMonths = this.year * 12 + this.month
    val otherTotalMonths = other.year * 12 + other.month
    return totalMonths >= otherTotalMonths
}
