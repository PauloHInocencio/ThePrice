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
    val id:Long = 0,
    val billId:Long,
    val day:Int,
    val month:Int,
    val year:Int,
    val paidValue:Int? = null,
    val paidAt:String? = null
)
