package br.com.noartcode.theprice.domain.model

data class Payment(
    val id: String = "",
    val billId: String,
    val dueDate: DayMonthAndYear,
    val price: Long,
    val isPayed:Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
