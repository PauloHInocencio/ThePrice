package br.com.noartcode.theprice.data.remote.datasource.bill

import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.BillWithPaymentsDto
import br.com.noartcode.theprice.data.remote.networking.safeCall
import br.com.noartcode.theprice.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url

class BillRemoteDataSourceImp(
    private val client:HttpClient
) : BillRemoteDataSource {

    override suspend fun fetchAllBills(): Resource<List<BillDto>> =
        safeCall {
            client.get{
                url("bills")
            }

        }

    override suspend fun post(billWithPayments: BillWithPaymentsDto): Resource<Unit> =
        safeCall {
            client.post{
                url("bills")
                setBody(billWithPayments)
            }
        }

    override suspend fun put(bill: BillDto): Resource<Unit> =
        safeCall {
            client.put {
                url("bills/${bill.id}")
                setBody(bill)
            }
        }

    override suspend fun delete(id: String): Resource<BillDto> =
        safeCall {
            client.delete {
                url("bills/$id")
            }
        }
}