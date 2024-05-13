package domain.model

data class ExpenseEntry(
    val id:Long,
    val expenseId:Long,
    val expenseTitle:String,
    val status: ExpenseStatus,
    val modifiedAt:String? = null
)
