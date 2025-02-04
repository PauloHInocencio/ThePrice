package br.com.noartcode.theprice.data.remote.networking

import br.com.noartcode.theprice.data.remote.dtos.NetworkError
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

const val API_BASE_URL = "http://192.168.1.7:8080/api/v1"

suspend inline fun <reified Request, reified Response: Any> HttpClient.patch(
    route: String,
    queryParameters: Map<String, Any?>,
    body: Request,
) : Resource<Response> {
    return safeCall {
        patch {
            url("$API_BASE_URL/$route")
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
            setBody(body)
        }
    }
}


suspend inline fun <reified Request, reified Response: Any> HttpClient.post(
    route: String,
    body: Request,
) : Resource<Response> {
    return safeCall {
        post {
            url("$API_BASE_URL/$route")
            setBody(body)
        }
    }
}

suspend inline fun <reified Response: Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
) : Resource<Response> {
    return safeCall {
        get {
            url("$API_BASE_URL/$route")
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }
}


suspend inline fun <reified Response: Any> HttpClient.delete(
    route: String,
    queryParameters: Map<String, Any?> = mapOf()
) : Resource<Response> {
    return safeCall {
         delete {
                url("$API_BASE_URL/$route")
                queryParameters.forEach { (key, value) ->
                    parameter(key, value)
                }
        }
    }
}

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


