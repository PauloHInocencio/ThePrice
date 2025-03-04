package br.com.noartcode.theprice.domain.model

data class Payment(
    val id: Long = 0L,
    val billId: String,
    val dueDate: DayMonthAndYear,
    val price: Long,
    val isPayed:Boolean,
)
