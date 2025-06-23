package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.payment.PaymentRemoteDataSource
import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

interface ISyncEventsWorker {
    fun start()
    fun stop()
}

class SyncEventsWorker(
    private val eventSyncQueue: EventSyncQueue,
    private val billRemoteDataSource: BillRemoteDataSource,
    private val paymentsDataSource: PaymentRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : ISyncEventsWorker {

    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())
    private var syncJob:Job? = null

    private val defaultJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }


    override fun start() {
        if (syncJob?.isActive == true) return

        syncJob = scope.launch {
            while (isActive) {
                val  event = eventSyncQueue.peek()
                if (event == null) {
                    delay(5_000)
                    continue
                }

                val result = when(Pair(event.endpoint, event.action)) {
                    "payment" to "create"-> {
                        val payments = defaultJson.decodeFromString<List<PaymentDto>>(event.payload)
                        paymentsDataSource.post(payments)
                    }
                    "payment" to "update" -> {
                        val payment = defaultJson.decodeFromString<PaymentDto>(event.payload)
                        paymentsDataSource.put(payment)
                    }
                    "bill" to "create" -> {
                        val bill = defaultJson.decodeFromString<BillDto>(event.payload)
                        billRemoteDataSource.post(bill)
                    }
                    "bill" to "update" -> {
                        val bill = defaultJson.decodeFromString<BillDto>(event.payload)
                        billRemoteDataSource.put(bill)
                    }
                    "bill" to "delete" -> {
                        val bill = defaultJson.decodeFromString<BillDto>(event.payload)
                        billRemoteDataSource.delete(bill.id)
                    }
                    else -> {
                        Resource.Error(message = "invalid unknown event: $event")
                    }
                }

                when (result) {
                    is Resource.Success -> eventSyncQueue.remove(event.id)
                    is Resource.Error -> result.exception?.printStackTrace()
                    Resource.Loading -> println("invalid result while sending event: $event")
                }
            }
        }
    }

    override fun stop() {
        syncJob?.cancel()
        syncJob = null
    }

}