package br.com.noartcode.theprice.domain.usecases

fun interface IDeleteBill : suspend (Long) -> Unit