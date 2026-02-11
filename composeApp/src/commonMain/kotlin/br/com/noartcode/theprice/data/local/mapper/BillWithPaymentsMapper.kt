package br.com.noartcode.theprice.data.local.mapper

import br.com.noartcode.theprice.data.remote.mapper.toDto
import br.com.noartcode.theprice.domain.model.BillWithPayments
import br.com.noartcode.theprice.domain.model.SyncEvent
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun BillWithPayments.toSyncEvent() : SyncEvent {
    val defaultJson = Json { ignoreUnknownKeys = true }
    return SyncEvent(
        id = Uuid.random().toString(),
        endpoint = "bill",
        action = "create",
        payload = defaultJson.encodeToString(this.toDto()),
        createdAt = this.bill.createdAt
    )
}
