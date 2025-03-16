package br.com.noartcode.theprice.data.remote.mapper

import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toLocalDate
import kotlinx.datetime.Instant

fun BillDto.toDomain() =
    Bill(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        type = Bill.Type.valueOf(this.type),
        status = Bill.Status.ACTIVE, // TODO("Should Get this information from the server as well")
        billingStartDate = this.billingStartDate.toDayMonthAndYear(),
        createdAt = Instant.parse(this.createdAt).toEpochMilliseconds(),
        updatedAt = Instant.parse(this.updatedAt).toEpochMilliseconds(),
        isSynced = this.isSynced,
    )

fun Iterable<BillDto>.toDomain() = this.map { it.toDomain() }


fun Bill.toDto() =
    BillDto(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        type = this.type.name,
        //status = this.status.name // TODO("Should Get this information from the server as well")
        billingStartDate = this.billingStartDate.toLocalDate().toString(),
        createdAt = Instant.fromEpochMilliseconds(this.createdAt).toString(),
        updatedAt = Instant.fromEpochMilliseconds(this.updatedAt).toString(),
        isSynced = this.isSynced,
    )