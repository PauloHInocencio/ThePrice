package br.com.noartcode.theprice.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import br.com.noartcode.theprice.data.local.dao.BillDao
import br.com.noartcode.theprice.data.local.dao.PaymentDao
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.local.entities.PaymentEntity

@Database(
    entities = [BillEntity::class, PaymentEntity::class],
    version = 1,
    exportSchema = false
)
@ConstructedBy(ThePrinceDatabaseConstructor::class)
abstract class ThePrinceDatabase : RoomDatabase() {
    abstract fun getBillDao (): BillDao
    abstract fun getPaymentDao() : PaymentDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ThePrinceDatabaseConstructor : RoomDatabaseConstructor<ThePrinceDatabase>

internal const val dbFileName = "the_price_app.db"