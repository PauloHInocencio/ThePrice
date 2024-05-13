package domain.model

data class Expense(
    val id:Long,
    val title:String,
    val description:String?,
    val invoiceDueDate:String
)
