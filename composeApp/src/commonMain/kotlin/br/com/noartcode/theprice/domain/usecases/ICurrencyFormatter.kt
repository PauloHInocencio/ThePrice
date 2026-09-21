package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.AppCurrency

interface ICurrencyFormatter {
    fun format(value:Long) : String
    fun clenup(value:String) : Long

}

expect class CurrencyFormatter(currency: AppCurrency) : ICurrencyFormatter