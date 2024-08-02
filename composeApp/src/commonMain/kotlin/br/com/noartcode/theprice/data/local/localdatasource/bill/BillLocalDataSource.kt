package br.com.noartcode.theprice.data.local.localdatasource.bill

import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

interface BillLocalDataSource {
    suspend fun getAllBills() : List<Bill>
    suspend fun getBillsBy(status:Bill.Status) : List<Bill>
    suspend fun insert(bill: Bill) : Long
    suspend fun getBill(id:Int) : Bill?
    suspend fun delete(id:Int)
}