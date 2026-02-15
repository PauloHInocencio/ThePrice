package br.com.noartcode.theprice.data.remote.datasource.bill

import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.data.remote.dtos.BillWithPaymentsDto
import br.com.noartcode.theprice.util.Resource

interface BillRemoteDataSource {

    suspend fun fetchAllBillsWithPayments() : Resource<List<BillWithPaymentsDto>>
    suspend fun post(billWithPayments: BillWithPaymentsDto) : Resource<Unit>
    suspend fun put(bill:BillDto) : Resource<Unit>
    suspend fun delete(id:String) : Resource<BillDto>
}