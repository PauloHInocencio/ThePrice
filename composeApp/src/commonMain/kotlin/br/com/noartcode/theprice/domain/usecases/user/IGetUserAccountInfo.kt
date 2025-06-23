package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.domain.model.User
import kotlinx.coroutines.flow.Flow


fun interface IGetUserAccountInfo : () -> Flow<User?>

