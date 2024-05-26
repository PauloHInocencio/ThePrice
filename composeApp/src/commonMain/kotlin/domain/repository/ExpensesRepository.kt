package domain.repository

import domain.model.Expense
import domain.model.Payment
import kotlinx.coroutines.flow.Flow
import util.Resource

interface ExpensesRepository {

    fun getAllExpenses() : Flow<Resource<List<Expense>>>

    suspend fun insert(
        title:String,
        invoiceDueDate:String,
        description:String? = null,
    )

    fun getAllPaymentsByMonth(
        month:Int,
        year:Int
    ) : Flow<Resource<List<Payment>>>

    fun getAllPaymentsOf(
        expenseId:Long
    ) : Flow<Resource<List<Payment>>>

    suspend fun insert(
        expenseId:Long,
        status:String,
        modifiedAt:String? = null
    )

    suspend fun getExpense(id:Long) : Resource<Expense>

    suspend fun update(expense: Expense)

    suspend fun delete(id:Long)
}