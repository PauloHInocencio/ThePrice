package domain.model

data class Expense(
    val id:Int,
    val title:String,
    val description:String?,
    val value:Float,
    val type: Type,
    val status: Status,
    val invoiceDueDate:Long
) {
    enum class Status {
        ACTIVE,
        CANCELLED,
        FINISHED
    }

    enum class Type {
        UNIQUE,
        MONTHLY,
    }
}
