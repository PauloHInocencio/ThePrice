package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.noartcode.theprice.data.local.entities.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Query("SELECT * FROM bills")
    suspend fun getAllBills(): List<BillEntity>

    @Query("SELECT * FROM bills WHERE status == :status")
    fun getBillsBy(status:String): Flow<List<BillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: BillEntity) : Long

    @Query("SELECT * FROM bills WHERE id == :id")
    suspend fun getBill(id:Long) : BillEntity?

    @Query("DELETE FROM bills WHERE id == :id")
    suspend fun deleteExpense(id:Long)
}