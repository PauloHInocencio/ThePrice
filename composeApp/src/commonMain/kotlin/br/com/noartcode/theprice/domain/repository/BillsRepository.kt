package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow


interface BillsRepository {
    fun getAllBills() : Flow<List<Bill>>
    fun getBillsBy(status:Bill.Status) : Flow<List<Bill>>
    suspend fun fetchAllBills() : Resource<Unit>
    suspend fun insert(bill: Bill) : String
    suspend fun post(bill:Bill) : Resource<Unit>
    suspend fun put(bill:Bill) : Resource<Unit>
    suspend fun get(id:String) : Bill?
    suspend fun update(bill:Bill)
    suspend fun deleteRemote(bill: Bill) : Resource<Unit>
    suspend fun deleteLocal(bill: Bill)
}