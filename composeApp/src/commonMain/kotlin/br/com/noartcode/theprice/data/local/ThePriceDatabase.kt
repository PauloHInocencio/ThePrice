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
@ConstructedBy(ThePriceDatabaseConstructor::class)
abstract class ThePriceDatabase : RoomDatabase() {
    abstract fun getBillDao (): BillDao
    abstract fun getPaymentDao() : PaymentDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ThePriceDatabaseConstructor : RoomDatabaseConstructor<ThePriceDatabase>{
    override fun initialize(): ThePriceDatabase
}

internal const val dbFileName = "the_price_app.db"