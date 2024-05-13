package domain.usecases

interface IGetMonthName {
    operator fun invoke(month:Int, year: Int) : String?
}

expect class GetMonthName : IGetMonthName