package br.com.noartcode.theprice.data.local.mapper


import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds

fun BillEntity.toDomain() =
    Bill(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        type = Bill.Type.valueOf(this.type),
        status = Bill.Status.valueOf(this.status),
        billingStartDate = this.billingStartDate.toDayMonthAndYear(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = this.isSynced,
    )

fun Bill.toEntity() =
    BillEntity(
        id = this.id,
        name = this.name.lowercase().trim(),
        description = this.description,
        price = this.price,
        type = this.type.name,
        status = this.status.name,
        billingStartDate = this.billingStartDate.toEpochMilliseconds(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = this.isSynced,
    )

fun Iterable<BillEntity>.toDomain() = this.map { it.toDomain() }

fun Iterable<Bill>.toEntity() = this.map { it.toEntity() }