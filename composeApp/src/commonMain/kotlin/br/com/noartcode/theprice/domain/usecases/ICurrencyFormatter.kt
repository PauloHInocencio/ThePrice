package br.com.noartcode.theprice.domain.usecases

interface ICurrencyFormatter {
    operator fun invoke(value:Int) : String
}

expect class CurrencyFormatter : ICurrencyFormatter