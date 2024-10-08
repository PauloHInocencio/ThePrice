package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.noartcode.theprice.data.local.entities.PaymentEntity

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments WHERE dueMonth == :month AND dueYear == :year")
    suspend fun getMonthPayments(month:Int, year:Int) : List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE billId == :billId")
    suspend fun getBillPayments(billId:Long) : List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE billId == :billId AND dueMonth == :month AND dueYear == :year")
    suspend fun getPayment(billId:Long, month:Int, year:Int) : PaymentEntity?

    @Query("SELECT * FROM payments WHERE id == :id")
    suspend fun getPayment(id:Long) : PaymentEntity?

    @Query("UPDATE payments SET paidValue = :paidValue, paidDay = :paidDay, paidMonth = :paidMonth, paidYear = :paidYear WHERE id == :paymentId")
    suspend fun updatePayment(
        paymentId:Long,
        paidValue:Int?,
        paidDay:Int?,
        paidMonth:Int?,
        paidYear:Int?
    )
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity) : Long

    @Query("DELETE FROM payments WHERE id == :id")
    suspend fun delete(id:Int)
}