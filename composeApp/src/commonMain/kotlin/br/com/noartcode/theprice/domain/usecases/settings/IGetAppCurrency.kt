package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.domain.model.AppCurrency
import kotlinx.coroutines.flow.Flow

fun interface IGetAppCurrency : () -> Flow<AppCurrency>