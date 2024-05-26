package domain.model

data class Payment(
    val id:Int,
    val expenseId:Long,
    val expenseTitle:String,
    val paidValue:Float,
    val status: Status,
    val paidAt:String? = null,
) {
    enum class Status {
        PAYED,
        PENDING
    }
}
