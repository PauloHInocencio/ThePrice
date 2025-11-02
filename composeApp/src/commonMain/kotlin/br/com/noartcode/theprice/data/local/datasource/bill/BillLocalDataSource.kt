package br.com.noartcode.theprice.data.local.datasource.bill

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface BillLocalDataSource {
    fun getAllBills() : Flow<List<Bill>>
    fun getBillsBy(status:Bill.Status) : Flow<List<Bill>>
    suspend fun update(bill: Bill)
    suspend fun insert(bills:List<Bill>)
    suspend fun insert(bill: Bill) : String
    suspend fun insertBillWithPayments(
        bill:Bill,
        payments:List<Payment>
    ) : Pair<Bill, List<Payment>>
    suspend fun getBill(id:String) : Bill?
    suspend fun delete(id:String)
    suspend fun deleteAllBills()
}