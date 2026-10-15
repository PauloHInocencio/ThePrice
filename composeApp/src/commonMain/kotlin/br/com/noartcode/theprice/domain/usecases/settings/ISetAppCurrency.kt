package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.domain.model.AppCurrency

interface ISetAppCurrency : suspend (AppCurrency) -> Unit