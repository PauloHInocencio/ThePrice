package br.com.noartcode.theprice.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title:String,
    val description:String? = null,
    val value:Float,
    val type:String,
    val status:String,
    val invoiceDueDate:String,
)