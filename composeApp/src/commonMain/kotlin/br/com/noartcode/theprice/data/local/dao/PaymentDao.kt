package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments WHERE dueMonth == :month AND dueYear == :year")
    fun getMonthPayments(month:Int, year:Int) : Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE billId == :billId")
    suspend fun getBillPayments(billId:String) : List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE billId == :billId AND dueMonth == :month AND dueYear == :year")
    suspend fun getPayment(billId:String, month:Int, year:Int) : PaymentEntity?

    @Query("SELECT * FROM payments WHERE id == :id")
    suspend fun getPayment(id:String) : PaymentEntity?

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payments: List<PaymentEntity>)

    @Query("DELETE FROM payments WHERE id == :id")
    suspend fun delete(id:Int)
}