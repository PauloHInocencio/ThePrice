package br.com.noartcode.theprice.data.remote.mapper

import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toLocalDate
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
        isSync = true //TODO("Not sure if I need get this information from the server")
    )

fun Iterable<PaymentDto>.toDomain() = this.map { it.toDomain() }

fun Payment.toDto() : PaymentDto =
    PaymentDto(
        id = this.id,
        billID = this.id,
        price = this.price,
        dueDate = this.dueDate.toLocalDate().toString(),
        isPayed = this.isPayed,
        createdAt = Instant.fromEpochMilliseconds(this.createdAt).toString(),
        updatedAt = Instant.fromEpochMilliseconds(this.updatedAt).toString(),
    )

fun Iterable<Payment>.toDto() = this.map { it.toDto() }