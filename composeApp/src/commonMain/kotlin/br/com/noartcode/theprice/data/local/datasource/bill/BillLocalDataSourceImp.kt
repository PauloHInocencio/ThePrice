package br.com.noartcode.theprice.data.local.datasource.bill

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.entities.BillEntity
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.usecases.IEpochMillisecondsFormatter
import br.com.noartcode.theprice.domain.usecases.IGetTodayDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class BillLocalDataSourceImp(
    private val database: ThePriceDatabase,
    private val epochFormatter:IEpochMillisecondsFormatter,
    private val getTodayDate: IGetTodayDate,
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

    override suspend fun insert(
        name:String,
        description:String?,
        price:Long,
        type:Bill.Type,
        status:Bill.Status,
        billingStartDate:DayMonthAndYear,
    ) : Long {
        return dao.insert(
            BillEntity(
                id = 0,
                name = name.lowercase().trim(),
                description = description,
                price = price,
                type = type.name,
                status = status.name,
                billingStartDate = billingStartDate.let { epochFormatter.from(it) },
                createdAt = getTodayDate().let { epochFormatter.from(it) }
            )
        )
    }

    override suspend fun update(bill: Bill) {
        dao.update(
            id = bill.id,
            name = bill.name,
            description = bill.description,
            price = bill.price,
            type = bill.type.name,
            status = bill.status.name,
            billingStartDate = bill.billingStartDate.let { epochFormatter.from(it) },
        )
    }

    override suspend fun getBill(id: Long): Bill? {
        return dao.getBill(id)?.toDomain(epochFormatter)
    }

    override suspend fun delete(id: Long) {
        dao.deleteBill(id)
    }
}