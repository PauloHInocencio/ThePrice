package br.com.noartcode.theprice.domain.usecases.sync

fun interface IGetPendingSyncCount {
    suspend operator fun invoke(): Int
}
