package br.com.noartcode.theprice.data.local.localdatasource

import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface BillLocalDataSource {
    fun getAllBills() : Flow<List<Bill>>
    fun getBillsBy(status:String) : Flow<List<Bill>>
    suspend fun insert(bill: Bill) : Long
    suspend fun getBill(id:Int) : Bill?
    suspend fun delete(id:Int)
}