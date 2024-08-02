package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.localdatasource.bill.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill

class BillLocalDataSourceFakeImp : BillLocalDataSource {
    private val store = mutableListOf<Bill>()
    override suspend fun getAllBills(): List<Bill> {
        TODO("Not yet implemented")
    }

    override suspend fun getBillsBy(status: Bill.Status): List<Bill> {
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