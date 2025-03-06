package br.com.noartcode.theprice.data.remote.mapper

import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import kotlinx.datetime.Instant

fun PaymentDto.toDomain() : Payment =
    Payment(
        id = this.id,
        billId = this.billID,
        price = this.price,
        dueDate = this.dueDate.toDayMonthAndYear(),
        isPayed = this.isPayed,
        createdAt = Instant.parse(this.createdAt).toEpochMilliseconds(),
        updatedAt = Instant.parse(this.updatedAt).toEpochMilliseconds(),
    )

fun Iterable<PaymentDto>.toDomain() = this.map { it.toDomain() }