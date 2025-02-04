package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.User
import kotlinx.coroutines.flow.Flow


fun interface IGetUserInfo : () -> Flow<User?>

