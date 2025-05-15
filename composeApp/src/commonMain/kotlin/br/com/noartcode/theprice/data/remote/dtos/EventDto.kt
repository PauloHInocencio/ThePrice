package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val table:String,
    val action:String,
    val data:String?
)
