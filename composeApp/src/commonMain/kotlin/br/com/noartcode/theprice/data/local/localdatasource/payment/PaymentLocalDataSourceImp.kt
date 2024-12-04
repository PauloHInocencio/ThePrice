package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PaymentLocalDataSourceImp (
    private val database: ThePriceDatabase
): PaymentLocalDataSource {

    private val dao by lazy { database.getPaymentDao() }

    override fun getMonthPayments(month: Int, year: Int): Flow<List<Payment>> = dao
        .getMonthPayments(month = month, year = year)
        .map { list -> list.map { it.toDomain() } }

    override suspend fun getBillPayments(billID: Long): List<Payment> {
        return dao
            .getBillPayments(billID)
            .map {
                it.toDomain()
            }
    }

    override suspend fun getPayment(billID: Long, month: Int, year: Int): Payment? {
        return dao
            .getPayment(billId = billID, month = month, year = year)
            ?.toDomain()
    }

    override suspend fun getPayment(id: Long): Payment? {
        return dao.getPayment(id)?.toDomain()
    }

    override suspend fun updatePayment(id:Long, dueDate: DayMonthAndYear, price:Int, isPayed: Boolean) {
        dao.updatePayment(
            paymentId = id,
            price = price,
            dueDay = dueDate.day,
            dueMonth = dueDate.month,
            dueYear = dueDate.year,
            isPayed = isPayed
        )
    }

    override suspend fun insert(billID: Long, dueDate: DayMonthAndYear, price:Int, isPayed:Boolean) : Long {
        return dao.insert(
            PaymentEntity(
                billId = billID,
                price = price,
                dueDay = dueDate.day,
                dueMonth = dueDate.month,
                dueYear = dueDate.year,
                isPayed = isPayed
            )
        )
    }



    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}