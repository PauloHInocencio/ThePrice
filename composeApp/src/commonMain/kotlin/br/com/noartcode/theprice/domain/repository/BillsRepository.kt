package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow


interface BillsRepository {

    fun getAllBills() : Flow<Resource<List<Bill>>>

    fun getBillsBy(status:String) : Flow<Resource<List<Bill>>>
    suspend fun insert(bill: Bill)
    suspend fun getBill(id:Int) : Resource<Bill>
    suspend fun delete(id:Int)
}