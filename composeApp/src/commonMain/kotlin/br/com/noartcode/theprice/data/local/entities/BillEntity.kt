package br.com.noartcode.theprice.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey
    val id:String,
    val name:String,
    val description:String? = null,
    val price:Long,
    val type:String,
    val status:String,
    val billingStartDate:Long,
    val createdAt:Long,
    val updatedAt:Long,
)