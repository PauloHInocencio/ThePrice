package br.com.noartcode.theprice.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class BillWithPaymentsDto(
    val bill: BillDto,
    val payments: List<PaymentDto>
)
