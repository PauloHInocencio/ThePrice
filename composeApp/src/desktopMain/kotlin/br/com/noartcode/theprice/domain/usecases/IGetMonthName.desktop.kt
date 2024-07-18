package br.com.noartcode.theprice.domain.usecases

actual class GetMonthName : IGetMonthName {
    override operator fun invoke(month:Int, year: Int) : String? = null
}