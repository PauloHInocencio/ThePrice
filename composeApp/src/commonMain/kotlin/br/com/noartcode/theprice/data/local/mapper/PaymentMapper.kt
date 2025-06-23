package br.com.noartcode.theprice.data.local.mapper

import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.data.remote.mapper.toDto
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.SyncEvent
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun PaymentEntity.toDomain() : Payment {
    return Payment(
        id = this.id,
        billId = this.billId,
        dueDate = DayMonthAndYear(day = this.dueDay, month = this.dueMonth, year = this.dueYear),
        price = this.price,
        isPayed = isPayed,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = this.isSynced,
    )
}

fun Iterable<PaymentEntity>.toDomain() = this.map { it.toDomain() }

fun Payment.toEntity() =
    PaymentEntity(
        id = this.id,
        billId = this.billId,
        isPayed = this.isPayed,
        price = this.price,
        dueDay = this.dueDate.day,
        dueMonth = this.dueDate.month,
        dueYear = this.dueDate.year,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = this.isSynced,
    )

fun Iterable<Payment>.toEntity() = this.map { model ->
    model.toEntity()
}

@OptIn(ExperimentalUuidApi::class)
fun Payment.toSyncEvent(action:String) : SyncEvent {
    val defaultJson = Json { ignoreUnknownKeys = true }
    return SyncEvent(
        id = Uuid.random().toString(),
        endpoint = "payment",
        action = action,
        payload = defaultJson.encodeToString(this.toDto()),
        createdAt = this.createdAt,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun Iterable<Payment>.toSyncEvent(action:String) : SyncEvent {
    val defaultJson = Json { ignoreUnknownKeys = true }
    return  SyncEvent(
        id = Uuid.random().toString(),
        endpoint = "payment",
        action = action,
        payload = defaultJson.encodeToString(this.toDto()),
        createdAt = this.last().createdAt,
    )
}
