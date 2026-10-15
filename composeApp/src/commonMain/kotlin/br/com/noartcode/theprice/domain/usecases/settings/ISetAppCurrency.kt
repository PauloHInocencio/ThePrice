package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.domain.model.AppCurrency

fun interface ISetAppCurrency : suspend (AppCurrency) -> Unit