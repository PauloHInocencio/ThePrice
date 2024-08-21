package br.com.noartcode.theprice.domain.model

data class Payment(
    val id:Long = 0L,
    val billId:Long,
    val billTitle:String,
    val dueDate:String,
    val paidValue:Int? = null,
    val paidAt:String? = null,
    val overdueDays:String? = null
) {
    enum class Status {
        PAYED,
        PENDING
    }
}
