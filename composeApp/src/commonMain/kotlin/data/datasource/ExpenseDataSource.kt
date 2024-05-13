package data.datasource

import data.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseDataSource {

    fun getAll() : Flow<List<ExpenseEntity>>

    suspend fun insert(
        title:String,
        invoiceDueDate:String,
        description:String? = null
    )

    suspend fun update(
        id:Long,
        title:String,
        invoiceDueDate:String,
        description:String? = null
    )

    suspend fun delete(id: Long)

}