package br.com.noartcode.theprice.data.local.datasource.bill

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.coroutines.flow.Flow

interface BillLocalDataSource {
    fun getAllBills() : Flow<List<Bill>>
    fun getBillsBy(status:Bill.Status) : Flow<List<Bill>>
    suspend fun update(bill: Bill)
    suspend fun insert(
        name:String,
        description:String?,
        price:Long,
        type:Bill.Type,
        status:Bill.Status,
        billingStartDate: DayMonthAndYear,
    ) : Long
    suspend fun getBill(id:Long) : Bill?
    suspend fun delete(id:Long)
}