package br.com.noartcode.theprice.data.helpers

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.SyncEvent
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds

val stubBills:List<Bill> by lazy {
    listOf(
        Bill(
            name = "Internet",
            description = "My internet bill.",
            price = 12099,
            billingStartDate = DayMonthAndYear(day = 5, month = 9, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Mom's Internet",
            description = "Mom's internet bill.",
            price = 9999,
            billingStartDate =  DayMonthAndYear(day = 5, month = 8, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Rent",
            description = "This is how much I currently pay for rent.",
            price = 220000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 7, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Health insurance",
            description = "Unimed's health insurance.",
            price = 190098,
            billingStartDate =  DayMonthAndYear(day = 5, month = 6, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Son's School",
            description = "My son's school",
            price = 80000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 5, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Condo",
            description = "Condominium of my rented apartment.",
            price = 55000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 4, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Credit Card",
            description = "Credit card bill.",
            price = 800000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 3, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Accountant",
            description = "My Accountant.",
            price = 800000,
            billingStartDate =  DayMonthAndYear(day = 5, month = 2, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        ),
        Bill(
            name = "Myself",
            description = "The money I save.",
            price = 0,
            billingStartDate =  DayMonthAndYear(day = 5, month = 1, year = 2024),
            createdAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            updatedAt = DayMonthAndYear(day = 19, month = 2, year = 2025).toEpochMilliseconds(),
            isSynced = false,
        )
    )
}


val stubPayments:List<Payment> by lazy {
    listOf(
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 1, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 2, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 3, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 4, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 5, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 6, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 7, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        ),
        Payment(
            billId = "",
            dueDate = DayMonthAndYear(day = 5, month = 8, year = 2024),
            price =12099,
            isPayed = false,
            createdAt = 0L,
            updatedAt = 0L,
            isSynced = false,
        )
    )
}


val stubSyncEvents: List<SyncEvent> by lazy {
    listOf(
        SyncEvent(
            id = "event-1",
            endpoint = "bill",
            action = "create",
            payload = """{"name":"Internet","price":12099}""",
            createdAt = 1000L,
        ),
        SyncEvent(
            id = "event-2",
            endpoint = "payment",
            action = "update",
            payload = """{"id":"payment-1","isPayed":true}""",
            createdAt = 2000L,
        ),
        SyncEvent(
            id = "event-3",
            endpoint = "bill",
            action = "delete",
            payload = """{"id":"bill-1"}""",
            createdAt = 3000L,
        ),
        SyncEvent(
            id = "event-4",
            endpoint = "payment",
            action = "create",
            payload = """{"dueDate":"2024-01-05","price":12099}""",
            createdAt = 4000L,
        ),
        SyncEvent(
            id = "event-5",
            endpoint = "bill",
            action = "update",
            payload = """{"id":"bill-2","price":15000}""",
            createdAt = 5000L,
        )
    )
}
