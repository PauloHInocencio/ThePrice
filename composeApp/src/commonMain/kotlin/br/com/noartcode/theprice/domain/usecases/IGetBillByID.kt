package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.Bill

fun interface IGetBillByID : suspend (String) -> Bill?