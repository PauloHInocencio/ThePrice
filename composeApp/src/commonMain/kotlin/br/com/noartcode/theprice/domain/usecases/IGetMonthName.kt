package br.com.noartcode.theprice.domain.usecases



interface IGetMonthName {
    operator fun invoke(month:Int) : String?
}

/**
 * I tried to use String Resources here, but did not work.
 * In order to get the values from an String-array generated from Compose-Resources,
 * I have to use a compose function.
 *
 * The best solution was to create an implementation for each target.
 */
expect class GetMonthName : IGetMonthName