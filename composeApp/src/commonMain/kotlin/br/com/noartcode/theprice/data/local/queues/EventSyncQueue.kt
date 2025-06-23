package br.com.noartcode.theprice.data.local.queues

import br.com.noartcode.theprice.domain.model.SyncEvent
import kotlinx.coroutines.flow.Flow

interface EventSyncQueue {
    val isEmpty : Flow<Boolean>
    suspend fun enqueue(event:SyncEvent)
    suspend fun peek(): SyncEvent?
    suspend fun remove(eventID:String)
}