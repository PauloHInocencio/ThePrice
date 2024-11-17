package br.com.noartcode.theprice.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val name:String,
    val description:String? = null,
    val price:Int,
    val type:String,
    val status:String,
    val billingStartDate:Long,
    val createdAt:Long,
)