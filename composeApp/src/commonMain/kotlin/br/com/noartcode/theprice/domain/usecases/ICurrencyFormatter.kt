package br.com.noartcode.theprice.domain.usecases

interface ICurrencyFormatter {
    fun format(value:Long) : String

    fun clenup(value:String) : Long

}

expect class CurrencyFormatter : ICurrencyFormatter