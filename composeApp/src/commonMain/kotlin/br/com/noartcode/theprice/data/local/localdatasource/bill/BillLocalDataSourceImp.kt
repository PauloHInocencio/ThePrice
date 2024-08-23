package br.com.noartcode.theprice.data.local.localdatasource.bill

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.domain.model.Bill
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

class BillLocalDataSourceImp(
    private val database: ThePrinceDatabase,
) : BillLocalDataSource {

    private val dao by lazy { database.getBillDao() }
    override suspend fun getAllBills(): List<Bill> {
        return dao.getAllBills().map { it.toDomain() }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getBillsBy(status: Bill.Status): Flow<List<Bill>> = dao
            .getBillsBy(status.name)
            .flatMapLatest { list ->
                flow{
                    emit(list.map { entity ->  entity.toDomain() })
                }
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
                invoiceDueDate = bill.invoiceDueDay
            )
        )
    }

    override suspend fun getBill(id: Long): Bill? {
        return dao.getBill(id)?.toDomain()
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}