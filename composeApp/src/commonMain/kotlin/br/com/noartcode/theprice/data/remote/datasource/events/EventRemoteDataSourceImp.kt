package br.com.noartcode.theprice.data.remote.datasource.events

import br.com.noartcode.theprice.data.remote.dtos.EventDto
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.url
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class EventRemoteDataSourceImp(
    private val client: HttpClient,
) : EventRemoteDataSource {

    override fun getEvents(): Flow<Resource<EventDto>> = callbackFlow {
        val session = client.sseSession {
            url("events")
        }
        val job = launch {
            session.incoming.collect { event ->
                val dto = event.data?.let { Json.decodeFromString<EventDto>(it) }
                val result = if (dto != null) {
                    Resource.Success(data = dto)
                } else {
                    Resource.Error(message = "Event parsing failed")
                }
                trySend(result)
            }
        }

        // Cancel the coroutine when the flow collector is cancelled
        awaitClose {
            job.cancel()
        }
    }
}