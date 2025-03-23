package br.com.noartcode.theprice.data.remote.datasource.payment

import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.util.Resource

interface PaymentRemoteDataSource {

    suspend fun fetchAllPayments() : Resource<List<PaymentDto>>
    suspend fun post(payments:List<PaymentDto>) : Resource<Unit>
}