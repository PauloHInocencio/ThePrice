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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
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
    ) : String {
        val billEntity = bill.copy(id = bill.id.ifEmpty { Uuid.random().toString() }).toEntity()
        dao.insert(billEntity)
        return billEntity.id
    }

    override suspend fun insertBillWithPayments(
        bill: Bill,
        payments: List<Payment>
    ) : Pair<Bill, List<Payment>> {
        val billEntity = bill.copy(id = bill.id.ifEmpty { Uuid.random().toString() }).toEntity()
        val paymentEntity = payments.map{ it.copy(id = it.id.ifEmpty { Uuid.random().toString() }) }.toEntity()
        dao.insertBillWithPayments(
            bill = billEntity,
            payments = paymentEntity
        )
        return billEntity.toDomain() to paymentEntity.toDomain()
    }

    override suspend fun update(bill: Bill) {

        dao.update(
            bill.toEntity()
        )
    }

    override suspend fun insert(bills: List<Bill>) {
        dao.insert(bills.toEntity())
    }

    override suspend fun getBill(id: String): Bill? {
        return dao.getBill(id)?.toDomain()
    }

    override suspend fun delete(id: String) {
        dao.deleteBill(id)
    }
}