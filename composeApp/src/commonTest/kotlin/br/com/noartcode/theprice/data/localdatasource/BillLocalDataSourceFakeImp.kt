package br.com.noartcode.theprice.data.localdatasource

import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.local.localdatasource.BillLocalDataSource
import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

class BillLocalDataSourceFakeImp : BillLocalDataSource {
    private val store = mutableListOf<BillEntity>()
    override fun getAllBills(): Flow<List<Bill>> {
        TODO("Not yet implemented")
    }

    override fun getBillsBy(status: String): Flow<List<Bill>> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(
        name: String,
        description: String?,
        price: Int,
        type: String,
        status: String,
        invoiceDueDate: Int
    ): Long {
        val billEntity = BillEntity(
            id = store.size.toLong() + 1,
            name = name,
            description = description,
            price = price,
            type = type,
            status = status,
            invoiceDueDate = invoiceDueDate,
        )
        store.add(billEntity)
        return billEntity.id
    }

    override suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}