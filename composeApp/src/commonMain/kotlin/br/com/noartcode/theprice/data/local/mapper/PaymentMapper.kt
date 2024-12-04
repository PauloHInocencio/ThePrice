package br.com.noartcode.theprice.data.local.mapper

import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment

fun PaymentEntity.toDomain() : Payment {
    return Payment(
        id = this.id,
        billId = this.billId,
        dueDate = DayMonthAndYear(day = this.dueDay, month = this.dueMonth, year = this.dueYear),
        price = this.price,
        isPayed = isPayed
    )
}