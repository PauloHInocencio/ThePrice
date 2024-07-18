package br.com.noartcode.theprice.domain.model

data class Payment(
    val id:Int,
    val billId:Long,
    val billTitle:String,
    val billInvoiceDueDate:Long,
    val paidValue:Float,
    val status: Status,
    val paidAt:Long? = null,
    val overdueDays:Int? = null
) {
    enum class Status {
        PAYED,
        PENDING
    }
}
