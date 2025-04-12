package br.com.noartcode.theprice.data.remote.datasource.payment

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.dtos.PaymentDto
import br.com.noartcode.theprice.data.remote.networking.safeCall
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.first

class PaymentRemoteDataSourceImp(
    private val client:HttpClient,
    private val session: SessionStorage
) : PaymentRemoteDataSource {

    override suspend fun fetchAllPayments(): Resource<List<PaymentDto>> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            client.get {
                url("payments")
                headers{
                  append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

    override suspend fun post(payments: List<PaymentDto>) : Resource<Unit> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            client.post {
                url("payments")
                setBody(payments)
                headers{
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

    override suspend fun put(payment: PaymentDto): Resource<Unit> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            client.put {
                url("payments/${payment.id}")
                setBody(payment)
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

}