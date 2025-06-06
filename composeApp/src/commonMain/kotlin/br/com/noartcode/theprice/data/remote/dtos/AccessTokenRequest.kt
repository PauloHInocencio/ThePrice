package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequest(
    @SerialName("refresh_token")
    val refreshToken:String,
    @SerialName("device_id")
    val deviceID:String,
)