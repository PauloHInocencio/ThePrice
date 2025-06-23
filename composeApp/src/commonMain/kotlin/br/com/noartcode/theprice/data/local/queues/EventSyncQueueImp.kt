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
    private val ioDispatcher: CoroutineDispatcher,
) : EventSyncQueue {

    private lateinit var _isEmpty:MutableStateFlow<Boolean>

    private val dao by lazy { database.getEventsDao() }

    override val isEmpty: Flow<Boolean>
        get() = _isEmpty

    private val scope = CoroutineScope( ioDispatcher + SupervisorJob())

    init {
        scope.launch {
            val initialValue = dao.count() == 0
            _isEmpty = MutableStateFlow(initialValue)
        }
    }

    override suspend fun enqueue(event: SyncEvent) {
        dao.enqueue(event.toEntity())
        _isEmpty.update { false }
    }

    override suspend fun peek(): SyncEvent? {
        return dao.peek()?.toDomain()
    }

    override suspend fun remove(eventID: String) {
        dao.remove(eventID)
        val count = dao.count()
        _isEmpty.update { count == 0}
    }
}