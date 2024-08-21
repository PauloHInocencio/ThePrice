package br.com.noartcode.theprice.data.local.localdatasource.bill

import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface BillLocalDataSource {
    suspend fun getAllBills() : List<Bill>
    fun getBillsBy(status:Bill.Status) : Flow<List<Bill>>
    suspend fun insert(bill: Bill) : Long
    suspend fun getBill(id:Long) : Bill?
    suspend fun delete(id:Long)
}