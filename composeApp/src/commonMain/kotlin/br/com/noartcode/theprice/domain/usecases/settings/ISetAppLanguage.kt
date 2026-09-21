package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.domain.model.AppLanguage

fun interface ISetAppLanguage : suspend (AppLanguage) -> Unit