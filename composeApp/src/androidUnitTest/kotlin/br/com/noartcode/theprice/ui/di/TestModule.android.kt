package br.com.noartcode.theprice.ui.di

import android.icu.text.DecimalFormatSymbols
import br.com.noartcode.theprice.domain.usecases.CurrencyFormatter
import br.com.noartcode.theprice.domain.usecases.ICurrencyFormatter
import br.com.noartcode.theprice.ui.presentation.newbill.NewBillViewModel
import io.mockk.every
import io.mockk.mockk
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

actual fun platformTestModule() = module {
    single<ICurrencyFormatter> {
        CurrencyFormatter(
            symbols = lazy {
                val mock = mockk<DecimalFormatSymbols>(relaxed = true)
                every { mock.decimalSeparator } returns ','
                every { mock.currencySymbol } returns  "R$"
                every { mock.groupingSeparator} returns '.'
                every { mock.zeroDigit } returns '0'
                mock
            }.value
        )
    }
    viewModel { NewBillViewModel(formatter = get()) }
}