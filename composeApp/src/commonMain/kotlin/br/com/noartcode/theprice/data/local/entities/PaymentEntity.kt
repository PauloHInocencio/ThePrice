package br.com.noartcode.theprice.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["billId"])
    ]
)
data class PaymentEntity(
    @PrimaryKey
    val id:String,
    val billId:String,
    val isPayed:Boolean,
    val dueDay:Int,
    val dueMonth:Int,
    val dueYear:Int,
    val price:Long,
    val createdAt:Long,
    val updatedAt:Long,
    val isSynced:Boolean,
)
