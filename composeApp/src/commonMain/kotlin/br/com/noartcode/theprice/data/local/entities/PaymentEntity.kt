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
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val billId:Int,
    val status:String,
    val month:Int,
    val year:Int,
    val paidValue:Float,
    val paidAt:String? = null
)
