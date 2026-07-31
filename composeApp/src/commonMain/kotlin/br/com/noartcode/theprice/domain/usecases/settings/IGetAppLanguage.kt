package br.com.noartcode.theprice.domain.usecases.settings

import br.com.noartcode.theprice.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

fun interface IGetAppLanguage : () -> Flow<AppLanguage>