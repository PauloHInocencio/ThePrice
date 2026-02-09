package br.com.noartcode.theprice.data.local.queues

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.data.local.mapper.toEntity
import br.com.noartcode.theprice.domain.model.SyncEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventSyncQueueImp(
    private val database: ThePriceDatabase,
) : EventSyncQueue {

    private val dao by lazy { database.getEventsDao() }

    private val _isEmpty = MutableStateFlow(true)
    override val isEmpty: Flow<Boolean>
        get() = _isEmpty


    override suspend fun enqueue(event: SyncEvent) {
        dao.enqueue(event.toEntity())
        _isEmpty.update { false }
    }

    override suspend fun peek(): SyncEvent? {
        return dao.peek()?.toDomain()
    }

    override suspend fun remove(eventID: String) {
        dao.remove(eventID)
        _isEmpty.update { size() == 0}
    }

    override suspend fun clean() {
        dao.clean()
        _isEmpty.update { size() == 0 }
    }

    override suspend fun size() : Int {
        return dao.count()
    }
}