package br.com.noartcode.theprice.data.remote.networking

import br.com.noartcode.theprice.data.remote.dtos.NetworkError
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

const val API_BASE_URL = "http://192.168.1.3:8080/api/v1/"



suspend inline fun <reified T> safeCall(execute: () -> HttpResponse) : Resource<T> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        e.printStackTrace()
        return Resource.Error(message = "No Internet", exception = e)
    } catch (e: SerializationException) {
        e.printStackTrace()
        return Resource.Error(message = "Couldn't serialize data from API", exception = e)
    }
    catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        return Resource.Error(message = "Error while making http request", exception = e)
    }

    return responseToResult(response)
}


suspend inline fun <reified T> responseToResult(response: HttpResponse) : Resource<T> {
    return when(val code = response.status.value) {
        in 200..299 -> runCatching {
            Resource.Success(data = response.body<T>())
        }.getOrElse { e ->
            e.printStackTrace()
            Resource.Error(message = "Failed to parse success response", exception = e)
        }
        else -> {
            val errorMessage = try {
                if (response.contentType()?.match(ContentType.Application.Json) == true) {
                    response.body<NetworkError?>()?.message ?: "Unknown error"
                } else {
                    response.bodyAsText()
                }
            } catch (e: Exception){
                e.printStackTrace()
                "Failed to parse error response"
            }

            Resource.Error(message = errorMessage, code = code)
        }
    }
}


