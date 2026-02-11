package br.com.noartcode.theprice.data.remote.mapper

import br.com.noartcode.theprice.data.remote.dtos.BillWithPaymentsDto
import br.com.noartcode.theprice.domain.model.BillWithPayments

fun BillWithPaymentsDto.toDomain() : BillWithPayments =
    BillWithPayments(
        bill = this.bill.toDomain(),
        payments = this.payments.toDomain()
    )

fun BillWithPayments.toDto() : BillWithPaymentsDto =
    BillWithPaymentsDto(
        bill = this.bill.toDto(),
        payments = this.payments.toDto()
    )