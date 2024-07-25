package br.com.noartcode.theprice.data.local.localdatasource

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

class BillLocalDataSourceImp(
    private val database: ThePrinceDatabase
) : BillLocalDataSource {

    private val dao by lazy {
        database.getBillDao()
    }

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
    ) : Long {
        return dao.insert(
            BillEntity(
                name = name,
                description = description,
                price = price,
                type = type,
                status = status,
                invoiceDueDate = invoiceDueDate
            )
        )
    }

    override suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}