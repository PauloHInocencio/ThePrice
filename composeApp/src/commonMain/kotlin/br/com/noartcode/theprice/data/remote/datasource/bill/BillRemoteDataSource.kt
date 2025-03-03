package br.com.noartcode.theprice.data.remote.datasource.bill

import br.com.noartcode.theprice.data.remote.dtos.BillDto
import br.com.noartcode.theprice.util.Resource

interface BillRemoteDataSource {

    suspend fun fetchAllBills() : Resource<List<BillDto>>

}