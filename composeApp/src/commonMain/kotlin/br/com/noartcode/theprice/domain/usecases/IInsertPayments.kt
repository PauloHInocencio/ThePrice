package br.com.noartcode.theprice.domain.usecases

import br.com.noartcode.theprice.domain.model.Payment

fun interface IInsertPayments : suspend (List<Payment>) -> Unit


