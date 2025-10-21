package br.com.noartcode.theprice.data.remote.mapper

import br.com.noartcode.theprice.data.remote.dtos.UserCredentialsDto
import br.com.noartcode.theprice.domain.model.User

fun UserCredentialsDto.toDomain() =
    User(
        name = this.name,
        email = this.email,
        picture = this.picture,
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )

fun Iterable<UserCredentialsDto>.toDomain() = this.map { it.toDomain() }