package br.com.noartcode.theprice.data.remote.networking

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

object ThePriceApiMock {

    val engine = MockEngine { request ->
        mockResponse(request) ?:errorResponse()
    }


    private fun MockRequestHandleScope.mockResponse(
        request: HttpRequestData
    ) : HttpResponseData? {
        val path = request.url.encodedPath
        val method = request.method

        if (path.contains("api/v1/bills") && method == HttpMethod.Get) {
            return respond(
                content = MockedApiResponses.GET_BILLS_MOCK_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        if (path.contains("api/v1/payments") && method == HttpMethod.Get) {
            return respond(
                content = MockedApiResponses.GET_PAYMENTS_MOCK_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        return null
    }


    private fun MockRequestHandleScope.errorResponse() : HttpResponseData {
        return respond(
            content = "",
            status = HttpStatusCode.BadRequest,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }
}

