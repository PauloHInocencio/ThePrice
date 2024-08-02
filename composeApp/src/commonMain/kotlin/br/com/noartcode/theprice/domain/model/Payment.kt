package br.com.noartcode.theprice.domain.model

data class Payment(
    val id:Long = 0L,
    val billId:Long,
    val billTitle:String,
    val paidValue:Int? = null,
    val paidAt:Long? = null,
    val overdueDays:Int? = null
) {
    enum class Status {
        PAYED,
        PENDING
    }
}
