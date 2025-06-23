package br.com.noartcode.theprice.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
class EventEntity(
    @PrimaryKey
    val id:String,
    val endpoint:String,
    val action:String,
    val payload:String,
    val createdAt:Long,
)