package br.com.noartcode.theprice.data.local.mapper

import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment

fun PaymentEntity.toDomain(billID:Long) : Payment {
    return Payment(
        id = this.id,
        billId = billID,
        dueDate = DayMonthAndYear(day = this.dueDay, month = this.dueMonth, year = this.dueYear),
        payedValue = this.paidValue,
        paidAt = if (this.paidValue != null)
            DayMonthAndYear(day = this.paidDay!!, month = this.paidMonth!!, year = this.paidYear!!)
        else null
    )
}