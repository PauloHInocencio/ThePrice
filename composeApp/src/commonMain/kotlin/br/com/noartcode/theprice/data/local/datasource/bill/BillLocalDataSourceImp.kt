package br.com.noartcode.theprice.data.local.datasource.bill

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.data.local.mapper.toEntity
import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.model.toEpochMilliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class BillLocalDataSourceImp(
    private val database: ThePriceDatabase,
) : BillLocalDataSource {

    private val dao by lazy { database.getBillDao() }
    override fun getAllBills(): Flow<List<Bill>> =
         dao.getAllBills().map { bills ->  bills.toDomain() }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getBillsBy(status: Bill.Status): Flow<List<Bill>> = dao
            .getBillsBy(status.name)
            .flatMapLatest { bills ->
                flow{
                    emit(bills.toDomain())
                }
            }

    override suspend fun insert(
        bill: Bill
    ) : Long {
        return dao.insert(bill.toEntity())
    }

    override suspend fun insertBillWithPayments(
        bill: Bill,
        payments: List<Payment>
    ) : Long {

        return dao.insertBillWithPayments(
            bill = bill.toEntity(),
            payments = payments.toEntity()
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
            billingStartDate = bill.billingStartDate.toEpochMilliseconds(),
        )
    }

    override suspend fun getBill(id: Long): Bill? {
        return dao.getBill(id)?.toDomain()
    }

    override suspend fun delete(id: Long) {
        dao.deleteBill(id)
    }
}