package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Query("SELECT * FROM bills")
    fun getAllBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE status == :status")
    fun getBillsBy(status:String): Flow<List<BillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bills:List<BillEntity>)

    @Update
    suspend fun update(bill:BillEntity)

    @Query("SELECT * FROM bills WHERE id == :id")
    suspend fun getBill(id:String) : BillEntity?

    @Query("DELETE FROM bills WHERE id == :id")
    suspend fun deleteBill(id:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments:List<PaymentEntity>)

    @Transaction
    suspend fun insertBillWithPayments(bill:BillEntity, payments:List<PaymentEntity>) {
        insert(bill)
        val paymentsWithBillId = payments.map {
            it.copy(billId = bill.id)
        }
        insertPayments(paymentsWithBillId)
    }
}