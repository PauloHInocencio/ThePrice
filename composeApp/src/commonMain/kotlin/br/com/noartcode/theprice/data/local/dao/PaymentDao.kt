package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.noartcode.theprice.data.local.entities.PaymentEntity

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments WHERE month == :month AND year == :year")
    suspend fun getMonthPayments(month:Int, year:Int) : List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE billId == :billId")
    suspend fun getBillPayments(billId:Int) : List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE billId == :billId AND month == :month AND year == :year")
    suspend fun getPayment(billId:Int, month:Int, year:Int) : PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id == :id")
    suspend fun delete(id:Int)
}