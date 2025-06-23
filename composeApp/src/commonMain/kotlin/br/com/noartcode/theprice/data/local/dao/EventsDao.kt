package br.com.noartcode.theprice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.noartcode.theprice.data.local.entities.EventEntity

@Dao
interface EventsDao {
    @Query("SELECT * FROM events ORDER BY createdAt ASC LIMIT 1")
    suspend fun peek() : EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(event: EventEntity)

    @Query("DELETE FROM events WHERE id == :id")
    suspend fun remove(id: String)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun count() : Int
}