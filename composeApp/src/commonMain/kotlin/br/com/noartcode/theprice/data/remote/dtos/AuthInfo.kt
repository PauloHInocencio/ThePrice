package br.com.noartcode.theprice.data.remote.dtos

data class AuthInfo(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)
