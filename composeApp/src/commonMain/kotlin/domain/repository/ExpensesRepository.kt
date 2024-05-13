package domain.repository

import domain.model.Expense
import domain.model.ExpenseEntry
import kotlinx.coroutines.flow.Flow
import util.Resource

interface ExpensesRepository {

    fun getAllExpenses() : Flow<Resource<List<Expense>>>

    suspend fun insert(
        title:String,
        invoiceDueDate:String,
        description:String? = null,
    )

    fun getAllEntriesByMonth(
        month:Int,
        year:Int
    ) : Flow<Resource<List<ExpenseEntry>>>

    fun getAllEntriesOf(
        expenseId:Long
    ) : Flow<Resource<List<ExpenseEntry>>>

    suspend fun insert(
        expenseId:Long,
        status:String,
        modifiedAt:String? = null
    )

    suspend fun getExpense(id:Long) : Resource<Expense>

    suspend fun update(expense: Expense)

    suspend fun delete(id:Long)
}