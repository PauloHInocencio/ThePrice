package br.com.noartcode.theprice.domain.usecases

import java.util.Calendar
import java.util.Locale

actual class GetMonthName : IGetMonthName {
    override operator fun invoke(month:Int, year: Int) : String? =
        Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
        }.getDisplayName(
            Calendar.MONTH,
            Calendar.LONG_FORMAT, Locale("pt")
        )?.replaceFirstChar { it.uppercase() }?.plus(" - $year")

}