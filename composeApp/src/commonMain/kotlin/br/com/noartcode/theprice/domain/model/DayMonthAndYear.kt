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
}

