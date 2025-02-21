package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Insert
    suspend fun insert(bill: BillEntity) : Long

    @Query(
        """
            UPDATE bills 
            SET name = :name, 
                description = :description, 
                price = :price, 
                type = :type, 
                status = :status, 
                billingStartDate = :billingStartDate 
            WHERE id == :id
        """
    )
    suspend fun update(
        id:Long,
        name:String,
        description:String?,
        price:Long,
        type:String,
        status:String,
        billingStartDate:Long
    )

    @Query("SELECT * FROM bills WHERE id == :id")
    suspend fun getBill(id:Long) : BillEntity?

    @Query("DELETE FROM bills WHERE id == :id")
    suspend fun deleteBill(id:Long)

    @Insert
    suspend fun insertPayments(payments:List<PaymentEntity>)

    @Transaction
    suspend fun insertBillWithPayments(bill:BillEntity, payments:List<PaymentEntity>) : Long {
        val billId = insert(bill)
        val paymentsWithBillId = payments.map {
            it.copy(billId = billId)
        }
        insertPayments(paymentsWithBillId)
        return billId
    }
}