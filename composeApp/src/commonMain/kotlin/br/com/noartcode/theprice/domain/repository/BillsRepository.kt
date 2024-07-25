package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow


interface BillsRepository {
    fun getAllBills() : Flow<List<Bill>>
    fun getBillsBy(status:String) : Flow<List<Bill>>
    suspend fun insert(bill: Bill)
    suspend fun getBill(id:Int) : Bill?
    suspend fun getBill(name:String) : Bill?
    suspend fun delete(id:Int)
}