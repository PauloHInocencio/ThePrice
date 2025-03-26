package br.com.noartcode.theprice.data.remote.datasource.bill

import br.com.noartcode.theprice.data.local.datasource.auth.SessionStorage
import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.mapper.toDto
import br.com.noartcode.theprice.data.remote.networking.safeCall
import br.com.noartcode.theprice.domain.model.Bill
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

class BillRemoteDataSourceImp(
    private val client:HttpClient,
    private val session: SessionStorage,
) : BillRemoteDataSource {

    override suspend fun fetchAllBills(): Resource<List<BillDto>> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            client.get{
                url("bills")
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }

        }

    override suspend fun post(bill: Bill): Resource<Unit> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            client.post{
                url("bills")
                setBody(bill.toDto())
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }

    override suspend fun put(bill: Bill): Resource<Unit> =
        safeCall {
            val accessToken = session.getAccessToken().first()
            client.put {
                url("bills")
                setBody(bill.toDto())
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }
            }
        }


}