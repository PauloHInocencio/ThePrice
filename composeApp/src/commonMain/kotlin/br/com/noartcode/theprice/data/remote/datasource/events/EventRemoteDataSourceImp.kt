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
        val job = try {
            val session = client.sseSession {
                url("events")
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
            launch {
                session.incoming.collect { event ->
                    val eventData = event.data
                    val result = if (eventData != null) {
                        Resource.Success(eventData)
                    } else {
                        Resource.Error(message = "Event data is null")
                    }
                    trySend(result)
                }
            }
        } catch (e:Throwable) {
            trySend(Resource.Error(message = "Failed to connect to SSE", exception = e))
            null
        }

        // Cancel the coroutine when the flow collector is cancelled
        awaitClose {
            job?.cancel()
        }
    }
}