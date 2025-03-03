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
    suspend fun insert(bill: Bill) : Long
    suspend fun insertBillWithPayments(
        bill:Bill,
        payments:List<Payment>
    ) : Long
    suspend fun getBill(id:Long) : Bill?
    suspend fun delete(id:Long)
}