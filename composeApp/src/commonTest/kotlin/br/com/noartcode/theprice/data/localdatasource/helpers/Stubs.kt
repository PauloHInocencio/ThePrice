package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds

val stubBills:List<Bill> by lazy {
    listOf(
        Bill(
            name = "Internet",
            description = "My internet bill.",
            price = 12099,
            billingStartDate = DayMonthAndYear(day = 5, month = 9, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Mom's Internet",
            description = "Mom's internet bill.",
            price = 9999,
            billingStartDate =  DayMonthAndYear(day = 5, month = 8, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Rent",
            description = "This is how much I currently pay for rent.",
            price = 220000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 7, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Health insurance",
            description = "Unimed's health insurance.",
            price = 190098,
            billingStartDate =  DayMonthAndYear(day = 5, month = 6, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Son's School",
            description = "My son's school",
            price = 80000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 5, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Condo",
            description = "Condominium of my rented apartment.",
            price = 55000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 4, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Credit Card",
            description = "Credit card bill.",
            price = 800000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 3, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Accountant",
            description = "My Accountant.",
            price = 800000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 2, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        ),
        Bill(
            name = "Myself",
            description = "The money I save.",
            price = 0,
            billingStartDate =  DayMonthAndYear(day = 5, month = 1, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds()
        )
    )
}


val stubPayments:List<Payment> by lazy {
    listOf(
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 1, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 2, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 3, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 4, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 5, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 6, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 7, year = 2024),
            price =12099,
            isPayed = false,
        ),
        Payment(
            billId = 1L,
            dueDate = DayMonthAndYear(day = 5, month = 8, year = 2024),
            price =12099,
            isPayed = false,
        )
    )
}
