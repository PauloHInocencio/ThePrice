package br.com.noartcode.theprice.domain.model

data class DayMonthAndYear(
    val day:Int,
    val month: Int,
    val year: Int
) : Comparable<DayMonthAndYear>{
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

