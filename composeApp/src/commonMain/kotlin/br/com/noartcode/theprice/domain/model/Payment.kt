package br.com.noartcode.theprice.domain.model

data class Payment(
    val id: Long = 0L,
    val billId: Long,
    val dueDate: DayMonthAndYear,
    val payedValue: Int? = null,
    val paidAt: DayMonthAndYear? = null
)
