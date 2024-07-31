package br.com.noartcode.theprice.domain.usecases

import platform.Foundation.NSCalendar
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual class GetMonthName(
    private val calendar:NSCalendar,
    private val locale: NSLocale = NSLocale.currentLocale()
) : IGetMonthName {
    override fun invoke(month: Int): String? {
        calendar.setLocale(locale)
        return calendar.monthSymbols()[month]?.toString()?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
    }
}