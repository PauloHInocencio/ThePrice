package br.com.noartcode.theprice.data.local.mapper

import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter

fun BillEntity.toDomain(
    epochFormatter: IEpochMillisecondsFormatter
) : Bill {
    return Bill(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        type = Bill.Type.valueOf(this.type),
        status = Bill.Status.valueOf(this.status),
        invoiceDueDay = this.invoiceDueDay,
        createAt = epochFormatter.to(this.createdAt)
    )
}