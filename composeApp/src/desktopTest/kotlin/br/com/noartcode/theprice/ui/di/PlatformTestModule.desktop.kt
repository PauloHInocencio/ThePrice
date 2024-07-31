package br.com.noartcode.theprice.ui.di

import br.com.noartcode.theprice.domain.usecases.GetMonthName
import br.com.noartcode.theprice.domain.usecases.IGetMonthName
import org.koin.dsl.module
import java.util.Calendar
import java.util.Locale

actual fun platformTestModule() = module {
    single<IGetMonthName>{ GetMonthName(calendar = Calendar.getInstance(), Locale("pt", "BR")) }
}