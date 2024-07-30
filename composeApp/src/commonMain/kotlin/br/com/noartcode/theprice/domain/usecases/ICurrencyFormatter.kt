package br.com.noartcode.theprice.domain.usecases

interface ICurrencyFormatter {
    fun format(value:Int) : String

    fun clenup(value:String) : Int

}

expect class CurrencyFormatter : ICurrencyFormatter