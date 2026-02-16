package br.com.noartcode.theprice.data.remote.datasource.payment

import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.util.Resource

interface PaymentRemoteDataSource {

    suspend fun post(payments:List<PaymentDto>) : Resource<Unit>
    suspend fun put(payment:PaymentDto) : Resource<Unit>
}