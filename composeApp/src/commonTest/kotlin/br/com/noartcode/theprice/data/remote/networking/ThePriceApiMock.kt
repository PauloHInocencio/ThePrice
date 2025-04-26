package br.com.noartcode.theprice.data.remote.networking

import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.readText
import kotlinx.serialization.json.Json

object ThePriceApiMock {

    const val BILL_FAILED_ID = "bill_failed_id"
    const val PAYMENT_FAILED_ID = "payment_failed_id"

    val engine = MockEngine { request ->
        mockResponse(request) ?:errorResponse()
    }


    private suspend fun MockRequestHandleScope.mockResponse(
        request: HttpRequestData
    ) : HttpResponseData? {
        val path = request.url.encodedPath
        val method = request.method
        val requestBody = extractRequestBody(request.body)
        if (path.contains("api/v1/bills") && method == HttpMethod.Get) {
            return respond(
                content = MockedApiResponses.GET_BILLS_MOCK_RESPONSE,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        if (path.contains("api/v1/bills") && method == HttpMethod.Post) {
            val bill = requestBody?.let { Json.decodeFromString<BillDto>(it) }
            if (bill == null || bill.id == BILL_FAILED_ID) return errorResponse()

            return respond(
                content = "",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        if (path.contains("api/v1/bills") && method == HttpMethod.Put) {
            val bill = requestBody?.let { Json.decodeFromString<BillDto>(it) }
            if (bill == null || bill.id == BILL_FAILED_ID) return errorResponse()

            return respond(
                content = "",
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

        if (path.contains("api/v1/payments") && method == HttpMethod.Post) {
            val payments = requestBody?.let { Json.decodeFromString<List<PaymentDto>>(it) }
            if (payments == null || payments.first().billID == BILL_FAILED_ID) return errorResponse()

            return respond(
                content = "",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        if(path.contains("api/v1/payments") && method == HttpMethod.Put) {
            val payment = requestBody?.let { Json.decodeFromString<PaymentDto>(it) }
            if (payment == null || payment.id == PAYMENT_FAILED_ID) return errorResponse()

            return respond(
                content = "",
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

private suspend fun extractRequestBody(body: OutgoingContent): String? {
    return when (body) {
        is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
        is OutgoingContent.ReadChannelContent -> body.readFrom().readRemaining().readText()
        is OutgoingContent.WriteChannelContent -> {
            val buffer = ByteChannel()
            body.writeTo(buffer)
            buffer.readRemaining().readText()
        }
        else -> null
    }
}