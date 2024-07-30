package br.com.noartcode.theprice.data.localdatasource

import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

class BillLocalDataSourceFakeImp : BillLocalDataSource {
    private val store = mutableListOf<Bill>()
    override fun getAllBills(): Flow<List<Bill>> {
        TODO("Not yet implemented")
    }

    override fun getBillsBy(status: String): Flow<List<Bill>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(bill: Bill): Long {
        return bill.copy(id = 1).also { store.add(it) }.id
    }

    override suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}