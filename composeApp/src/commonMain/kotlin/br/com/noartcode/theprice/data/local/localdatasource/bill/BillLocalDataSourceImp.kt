package br.com.noartcode.theprice.data.local.localdatasource.bill

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.flow.Flow

class BillLocalDataSourceImp(
    private val database: ThePrinceDatabase
) : BillLocalDataSource {

    private val dao by lazy { database.getBillDao() }
    override suspend fun getAllBills(): List<Bill> {
        TODO("Not yet implemented")
    }

    override suspend fun getBillsBy(status: Bill.Status): List<Bill> {
        TODO("Not yet implemented")
    }


    override suspend fun insert(bill: Bill) : Long {
        val normalizedName = bill.name.trim().lowercase()

        return dao.insert(
            BillEntity(
                name = normalizedName,
                description = bill.description,
                price = bill.price,
                type = bill.type.name,
                status = bill.status.name,
                invoiceDueDate = bill.invoiceDueDate
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