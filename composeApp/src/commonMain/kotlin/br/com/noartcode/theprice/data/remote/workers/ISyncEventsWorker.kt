package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.data.local.datasource.auth.AuthLocalDataSource
import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.data.remote.datasource.payment.PaymentRemoteDataSource
import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.BillWithPaymentsDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
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
    private val session: AuthLocalDataSource,
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
            eventSyncQueue.isEmpty.collect { isEmpty ->
                while (isActive && isEmpty.not()) {

                    val accessToken = session.getAccessToken().first()
                    if (accessToken?.isNotEmpty() == true) {
                       println("No valid access token, stopping sync worker")
                       break
                    }


                    val  event = eventSyncQueue.peek()
                    if (event == null) {
                        println("No new events to sync...")
                        break
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
                            val billWithPayments = defaultJson.decodeFromString<BillWithPaymentsDto>(event.payload)
                            billRemoteDataSource.post(billWithPayments)
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
                        is Resource.Success ->  {
                            eventSyncQueue.remove(event.id)
                            println("Success sync event: ${event.id}")
                        }
                        is Resource.Error -> {
                            println("Error during synchronization: ${result.message}")
                            result.exception?.printStackTrace()
                            delay(5_000) // TODO("Find a better way to retry, maybe based on exponential attempts")
                        }
                        is Resource.Loading -> println("invalid result while sending event: $event")
                    }
                }
            }
        }
    }

    override fun stop() {
        syncJob?.cancel()
        syncJob = null
    }

}