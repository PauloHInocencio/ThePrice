package br.com.noartcode.theprice.domain.model

data class SyncEvent(
    val id:String,
    val endpoint:String,
    val action:String,
    val payload:String,
    val createdAt:Long,
)