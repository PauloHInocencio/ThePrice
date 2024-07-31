package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.dsl.module
import platform.Foundation.NSCalendar
import platform.Foundation.NSLocale

actual fun platformTestModule() = module{
    single<IGetMonthName> { GetMonthName(
        calendar = NSCalendar.currentCalendar(),
        locale = NSLocale("pt_BR")
    )}
}