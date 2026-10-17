package br.com.noartcode.theprice.data.remote.networking

import br.com.noartcode.theprice.data.remote.dtos.AccessTokenRequest
import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.data.remote.networking.MockedApiResponses.userCreationMockResponse
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object ThePriceApiMock {
    const val BILL_FAILED_ID = "bill_failed_id"
    const val PAYMENT_FAILED_ID = "payment_failed_id"
    const val MOCK_EMAIL = "mock_test@email"
    const val MOCK_NAME = "Test Silva"
    const val EXPIRED_TOKEN = "expired_access_token"
    const val RENEWED_TOKEN = "renewed_access_token"
    const val EXPIRED_REFRESH_TOKEN = "expired_refresh_token"
    const val REFRESH_SERVER_ERROR_TOKEN = "refresh_server_error_token"
    const val LOGOUT_FAILED_REFRESH_TOKEN = "logout_failed_refresh_token"

    val engine = MockEngine { request ->
        mockResponse(request)
    }

    private interface AuthorizedRoute

    private sealed interface ApiRoute {
        data object GetBills : ApiRoute, AuthorizedRoute
        data object PostBill : ApiRoute, AuthorizedRoute
        data object PutBill : ApiRoute, AuthorizedRoute
        data object GetPayments : ApiRoute, AuthorizedRoute
        data object PostPayments : ApiRoute, AuthorizedRoute
        data object PutPayment : ApiRoute, AuthorizedRoute
        data object LogoutUser : ApiRoute, AuthorizedRoute
        data object RenewAccessToken : ApiRoute
        data object PostUser: ApiRoute
        data object LoginUser: ApiRoute
        data object Unknown : ApiRoute
    }

    private fun matchRoute(path: String, method: HttpMethod): ApiRoute = when {
        path.contains("api/v1/bills") && method == HttpMethod.Get -> ApiRoute.GetBills
        path.contains("api/v1/bills") && method == HttpMethod.Post -> ApiRoute.PostBill
        path.contains("api/v1/bills") && method == HttpMethod.Put -> ApiRoute.PutBill
        //path.contains("api/v1/payments") && method == HttpMethod.Get -> ApiRoute.GetPayments // This doesn't exist anymore
        path.contains("api/v1/payments") && method == HttpMethod.Post -> ApiRoute.PostPayments
        path.contains("api/v1/payments") && method == HttpMethod.Put -> ApiRoute.PutPayment
        path.contains("api/v1/users/create") && method == HttpMethod.Post -> ApiRoute.PostUser
        path.contains("api/v1/users/login") && method == HttpMethod.Post -> ApiRoute.LoginUser
        path.contains("api/v1/users/logout") && method == HttpMethod.Post -> ApiRoute.LogoutUser
        path.contains("api/v1/users/renew-access-token") && method == HttpMethod.Post -> ApiRoute.RenewAccessToken
        else -> ApiRoute.Unknown
    }

    private suspend fun MockRequestHandleScope.mockResponse(
        request: HttpRequestData
    ): HttpResponseData {
        val path = request.url.encodedPath
        val method = request.method
        val requestBody = extractRequestBody(request.body)
        val token = request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer")?.trim()
        val route = matchRoute(path, method)
        if (route is AuthorizedRoute && token.isNullOrBlank()) {
            return errorResponse("Unauthorized Request")
        }
        if (route is AuthorizedRoute && token == EXPIRED_TOKEN) {
            return unauthorizedResponse()
        }
        return when (route) {
            // Bills endpoints
            ApiRoute.GetBills -> {
                respond(
                    content = MockedApiResponses.GET_BILLS_MOCK_RESPONSE,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.PostBill -> {
                val bill = requestBody?.let { Json.decodeFromString<BillDto>(it) }
                if (bill == null || bill.id == BILL_FAILED_ID) {
                    return errorResponse()
                }

                respond(
                    content = "",
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.PutBill -> {
                val bill = requestBody?.let { Json.decodeFromString<BillDto>(it) }
                if (bill == null || bill.id == BILL_FAILED_ID) {
                    return errorResponse()
                }

                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            // Payments endpoints
            ApiRoute.GetPayments -> {
                if (token?.isBlank() == true) return errorResponse()

                respond(
                    content = MockedApiResponses.GET_PAYMENTS_MOCK_RESPONSE,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.PostPayments -> {
                val payments = requestBody?.let { Json.decodeFromString<List<PaymentDto>>(it) }
                if (payments == null || payments.first().billID == BILL_FAILED_ID) {
                    return errorResponse()
                }

                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.PutPayment -> {
                val payment = requestBody?.let { Json.decodeFromString<PaymentDto>(it) }
                if (payment == null || payment.id == PAYMENT_FAILED_ID) {
                    return errorResponse()
                }

                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.PostUser ->  {
                val payload = requestBody?.let { Json.decodeFromString<CreateUserPayloadDto>(it) }
                if (payload == null) return errorResponse("Invalid request body")
                respond(
                    content = userCreationMockResponse(payload.name, payload.email),
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.LoginUser -> {
                val payload = requestBody?.let { Json.decodeFromString<LoginUserPayLoadDto>( it) }
                if (payload == null) return errorResponse("Invalid request body")
                respond(
                    content = userCreationMockResponse(name = MOCK_NAME, email = MOCK_EMAIL),
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.LogoutUser -> {
                val payload = requestBody?.let {  Json.decodeFromString<LogoutUserPayloadDto>(it) }
                if (payload == null ) return errorResponse("Invalid request body")
                if (payload.refreshToken == LOGOUT_FAILED_REFRESH_TOKEN) {
                    return errorResponse("Internal server error", HttpStatusCode.InternalServerError)
                }
                respond(
                    content = "",
                    status = HttpStatusCode.NoContent,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }

            ApiRoute.RenewAccessToken -> {
                val payload = requestBody?.let { Json.decodeFromString<AccessTokenRequest>(it) }
                when (payload?.refreshToken) {
                    null -> errorResponse("Invalid request body")
                    EXPIRED_REFRESH_TOKEN -> unauthorizedResponse()
                    REFRESH_SERVER_ERROR_TOKEN -> errorResponse("Internal server error", HttpStatusCode.InternalServerError)
                    else -> respond(
                        content = """{"access_token":"$RENEWED_TOKEN"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }

            ApiRoute.Unknown -> errorResponse()

        }
    }

    private fun MockRequestHandleScope.errorResponse(
        errorMessage: String = "Unknow API path",
        status: HttpStatusCode = HttpStatusCode.BadRequest,
    ): HttpResponseData {
        return respond(
            content = """
                {"message":"$errorMessage"}
            """.trimIndent(),
            status = status,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    // The WWW-Authenticate header makes Ktor's bearer provider try to refresh the token
    private fun MockRequestHandleScope.unauthorizedResponse(): HttpResponseData {
        return respond(
            content = """{"message":"token is expired"}""",
            status = HttpStatusCode.Unauthorized,
            headers = headersOf(
                HttpHeaders.ContentType to listOf("application/json"),
                HttpHeaders.WWWAuthenticate to listOf("Bearer"),
            )
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

@Serializable
private data class CreateUserPayloadDto(
    val name: String,
    val email: String,
    val password: String,
    @SerialName("device_id")
    val deviceID:String
)

@Serializable
private data class LoginUserPayLoadDto(
    @SerialName("token_id")
    val tokenID: String,
    @SerialName("raw_nonce")
    val rawNonce: String,
    @SerialName("device_id")
    val deviceID: String
)

@Serializable
private data class LogoutUserPayloadDto(
    @SerialName("refresh_token")
    val refreshToken:String,
    @SerialName("device_id")
    val deviceID: String
)