package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class NetworkError(
    val message:String
)
