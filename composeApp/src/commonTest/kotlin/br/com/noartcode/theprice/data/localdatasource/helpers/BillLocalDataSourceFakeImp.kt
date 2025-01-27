package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.datasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BillLocalDataSourceFakeImp : BillLocalDataSource {
    private val store = mutableListOf<Bill>()
    override fun getAllBills(): Flow<List<Bill>> = flow {
        emit(store)
    }

    override fun getBillsBy(status: Bill.Status): Flow<List<Bill>> = flow {
       emit(store.filter { it.status == status })
    }

    override suspend fun insert(
        name: String,
        description: String?,
        price: Long,
        type: Bill.Type,
        status: Bill.Status,
        billingStartDate: DayMonthAndYear
    ): Long {
        return 1
    }

    override suspend fun update(bill: Bill) {
        TODO("Not yet implemented")
    }

    override suspend fun getBill(id: Long): Bill? {
        return store.singleOrNull { it.id == id }
    }

    override suspend fun delete(id: Long) {
        store.removeAll { it.id == id }
    }
}