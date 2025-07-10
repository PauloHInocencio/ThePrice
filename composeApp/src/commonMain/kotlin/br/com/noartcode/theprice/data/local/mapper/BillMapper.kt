package br.com.noartcode.theprice.data.local.mapper


import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.remote.mapper.toDto
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.SyncEvent
import br.com.noartcode.theprice.domain.model.toDayMonthAndYear
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@OptIn(ExperimentalUuidApi::class)
fun Bill.toSyncEvent(action:String) : SyncEvent {
    val defaultJson = Json { ignoreUnknownKeys = true }
    return SyncEvent(
        id = Uuid.random().toString(),
        endpoint = "bill",
        action = action,
        payload = defaultJson.encodeToString(this.toDto()),
        createdAt = this.createdAt
    )
}

