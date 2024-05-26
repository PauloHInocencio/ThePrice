package data.datasource

import data.entities.PaymentEntity
import kotlinx.coroutines.flow.Flow

interface PaymentDataSource {

    fun getAll() : Flow<List<PaymentEntity>>

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