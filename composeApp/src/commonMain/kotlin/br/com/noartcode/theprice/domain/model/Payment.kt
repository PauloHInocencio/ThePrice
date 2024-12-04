package br.com.noartcode.theprice.domain.model

data class Payment(
    val id: Long = 0L,
    val billId: Long,
    val dueDate: DayMonthAndYear,
    val price: Int,
    val isPayed:Boolean,
)
