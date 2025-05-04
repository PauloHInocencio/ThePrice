package br.com.noartcode.theprice.ui.presentation.account

import io.ktor.http.ContentType
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

class KtorAuthServer {
    private var code: String? = null

    suspend fun listenForCode(): String? {
        val server = embeddedServer(Netty, port = 8888) {
            routing {
                get("/callback") {
                    code = call.request.queryParameters["code"]
                    call.respondText("You can close this window.", ContentType.Text.Html)
                }
            }
        }
        server.start(wait = false)

        while (code == null) {
            kotlinx.coroutines.delay(100)
        }

        server.stop(0L, 0L)
        return code
    }
}