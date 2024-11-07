package br.com.noartcode.theprice.data.local.localdatasource.bill

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.usecases.GetTodayDate
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class BillLocalDataSourceImp(
    private val database: ThePrinceDatabase,
    private val epochFormatter:IEpochMillisecondsFormatter
) : BillLocalDataSource {

    private val dao by lazy { database.getBillDao() }
    override fun getAllBills(): Flow<List<Bill>> =
         dao.getAllBills().map { bills ->  bills.map { it.toDomain(epochFormatter) } }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getBillsBy(status: Bill.Status): Flow<List<Bill>> = dao
            .getBillsBy(status.name)
            .flatMapLatest { list ->
                flow{
                    emit(list.map { entity ->  entity.toDomain(epochFormatter) })
                }
            }

    override suspend fun insert(bill: Bill) : Long {
        val normalizedName = bill.name.trim().lowercase()

        return dao.insert(
            BillEntity(
                id = bill.id,
                name = normalizedName,
                description = bill.description,
                price = bill.price,
                type = bill.type.name,
                status = bill.status.name,
                createdAt = bill.createAt.let { epochFormatter.from(it) }
            )
        )
    }

    override suspend fun getBill(id: Long): Bill? {
        return dao.getBill(id)?.toDomain(epochFormatter)
    }

    override suspend fun delete(id: Long) {
        dao.deleteBill(id)
    }
}