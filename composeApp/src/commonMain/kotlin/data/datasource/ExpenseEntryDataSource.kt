package data.datasource

import data.entities.ExpenseEntryEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseEntryDataSource {

    fun getAll() : Flow<List<ExpenseEntryEntity>>

    suspend fun insert(
        expenseId:Long,
        status:String,
        modifiedAt:String? = null
    )

    suspend fun update(
        id:Long,
        expenseId:Long,
        status:String,
        modifiedAt:String? = null
    )

    suspend fun delete(id: Long)


}