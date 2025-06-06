package br.com.noartcode.theprice.data.remote.datasource.events

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.dtos.EventDto
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class EventRemoteDataSourceImp(
    private val client: HttpClient,
    private val session: SessionStorage,
) : EventRemoteDataSource {

    override fun getEvents(): Flow<Resource<String>> = callbackFlow {
        val accessToken = session.getAccessToken().first()
        val session = client.sseSession {
            url("events")
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }
        val job = launch {
            session.incoming.collect { event ->
               /* val dto = event.data?.let { Json.decodeFromString<EventDto>(it) }
                val result = if (dto != null) {
                    Resource.Success(data = dto)
                } else {
                    Resource.Error(message = "Event parsing failed")
                }*/
                val eventData = event.data
                val result = if (eventData != null) {
                    Resource.Success(eventData)
                } else {
                    Resource.Error(message = "Event aata is null")
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