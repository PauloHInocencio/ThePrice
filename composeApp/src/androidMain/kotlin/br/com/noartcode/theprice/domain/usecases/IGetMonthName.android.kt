package br.com.noartcode.theprice.domain.usecases

import java.util.Calendar
import java.util.Locale

actual class GetMonthName(
    private val calendar: Calendar,
    private val locale: Locale = Locale.getDefault()
) : IGetMonthName {
    override fun invoke(month: Int): String? {
        //TODO : Change the implementation to use joda-time instead of Calendar
        //https://www.joda.org/joda-time/quickstart.html
        calendar.set(Calendar.MONTH, month - 1)
        return calendar
            .getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, locale)
            ?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
    }
}