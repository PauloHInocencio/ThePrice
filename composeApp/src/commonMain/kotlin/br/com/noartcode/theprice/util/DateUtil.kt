package br.com.noartcode.theprice.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

object DateUtil {

    fun addOneDay(date: String) : String {
        return date
            .toLocalDate()
            .plus(1, DateTimeUnit.DAY)
            .toString()

    }

    fun removeOneDay(date: String) : String {
        return date
            .toLocalDate()
            .minus(1, DateTimeUnit.DAY)
            .toString()
    }

    fun getToday() : String {
        val timeZone = TimeZone.currentSystemDefault()
        return Clock.System.todayIn(timeZone).toString()
    }

    fun getDateMonthAndYear(date: String) : br.com.noartcode.theprice.domain.model.DayMonthAndYear {
        val ld = date.toLocalDate()
        return br.com.noartcode.theprice.domain.model.DayMonthAndYear(
            month = ld.monthNumber,
            year = ld.year,
            day = ld.dayOfMonth
        )
    }


    fun getDayOfWeek(date: String) : Int {
        return date
            .toLocalDate()
            .dayOfWeek.ordinal
    }

    fun isLessOrEqual(d1:String, d2:String) : Boolean {
        return d1.toLocalDate() <= d2.toLocalDate()
    }

    fun isLess(d1:String, d2:String) : Boolean {
        return d1.toLocalDate() < d2.toLocalDate()
    }

    fun isGreater(d1:String, d2:String) : Boolean {
        return d1.toLocalDate() > d2.toLocalDate()
    }


    fun formatHeaderDate(date: String) : String {
        val _date = date.toLocalDate()
        val now = Clock.System.now()
        val systemTZ = TimeZone.currentSystemDefault()


        val today = now.toLocalDateTime(systemTZ)
        val yesterday = now.minus(1, DateTimeUnit.DAY, systemTZ).toLocalDateTime(systemTZ)
        val tomorrow = now.plus(1, DateTimeUnit.DAY, systemTZ).toLocalDateTime(systemTZ)

        val month = _date.month.name.lowercase().take(3).replaceFirstChar { it.uppercase() }
        val day = if (_date.dayOfMonth < 10) "0${_date.dayOfMonth}" else _date.dayOfMonth
        val year = _date.year



        return when(_date) {
            today.date -> "Hoje"
            yesterday.date -> "Ontem"
            tomorrow.date -> "Amanhã"
            else -> buildString {
                append(day)
                append(" ")
                append(month)
                append(" ")
                append(year)
            }
        }
    }


    fun getMonthDays(month:Int, year:Int) : List<List<String?>> {

        val days:MutableList<MutableList<String?>> = mutableListOf()

        val firstDayOfTheMonth = LocalDate(year = year, monthNumber = month, dayOfMonth = 1)

        // Start with the first sunday of the first week.
        var currentDay = firstDayOfTheMonth
            .minus(firstDayOfTheMonth.dayOfWeek.ordinal, DateTimeUnit.DAY) // to get monday.
            .minus(1, DateTimeUnit.DAY) // to get sunday


        // creates 6 rows
        while (days.size < 6) {
            val week = mutableListOf<String?>()
            var day: LocalDate? = null

            for (i in 0 until  7) {

                day = currentDay.plus(i, DateTimeUnit.DAY)

                if (day.monthNumber == month) {
                    week.add(day.toString())
                } else {
                    week.add(null)
                }

            }

            days.add(week)
            currentDay = day!!.plus(1, DateTimeUnit.DAY) // to get next sunday
        }


        // removes the row that all elements are null
        if (days[0].all { it == null }) {
            days.removeAt(0)
        }

        if(days.last().all { it == null }){
            days.removeLast()
        }

        return days

    }
}
