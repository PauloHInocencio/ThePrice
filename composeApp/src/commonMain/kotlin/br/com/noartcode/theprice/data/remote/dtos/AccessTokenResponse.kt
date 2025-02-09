package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AccessTokenResponse(
    @SerialName("access_token")
    val accessToken:String,
)
