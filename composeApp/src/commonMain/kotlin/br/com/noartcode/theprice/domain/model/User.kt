package br.com.noartcode.theprice.domain.model

data class User(
    val name:String,
    val email:String,
    val picture:String,
    val accessToken:String,
    val refreshToken:String,
)
