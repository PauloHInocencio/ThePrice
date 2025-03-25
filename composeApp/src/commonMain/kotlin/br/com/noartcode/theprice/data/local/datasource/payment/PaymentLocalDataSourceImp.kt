package br.com.noartcode.theprice.data.local.datasource.payment

import br.com.noartcode.theprice.data.local.ThePriceDatabase
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.data.local.mapper.toEntity
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PaymentLocalDataSourceImp (
    private val database: ThePriceDatabase
): PaymentLocalDataSource {

    private val dao by lazy { database.getPaymentDao() }

    override fun getMonthPayments(month: Int, year: Int): Flow<List<Payment>> {
        return dao
            .getMonthPayments(month = month, year = year)
            .map { it.toDomain() }
    }

    override fun getAllPayments(): Flow<List<Payment>> {
        return dao
            .getAll()
            .map { it.toDomain() }
    }


    override suspend fun getBillPayments(billID: String): List<Payment> {
        return dao
            .getBillPayments(billID)
            .toDomain()
    }

    override suspend fun getNotSynchronizedPayments(): List<Payment> {
        return dao.getNotSynchronizedPayments().toDomain()
    }

    override suspend fun getPayment(billID: String, month: Int, year: Int): Payment? {
        return dao
            .getPayment(billId = billID, month = month, year = year)
            ?.toDomain()
    }

    override suspend fun getPayment(id: String): Payment? {
        return dao.getPayment(id)?.toDomain()
    }

    override suspend fun update(payment: Payment) {
        dao.update(payment.toEntity())
    }

    override suspend fun update(payments: List<Payment>) {
        dao.update(payments.toEntity())
    }

    override suspend fun insert(payment:Payment) : String {
        val paymentEntity = payment.copy(id = payment.id.ifEmpty { Uuid.random().toString() }).toEntity()
        dao.insert(paymentEntity)
        return paymentEntity.id

    }

    override suspend fun insert(payments: List<Payment>) {
        return dao.insert(
            payments.map {
                it.copy(id = it.id.ifEmpty { Uuid.random().toString() })
            }.toEntity()
        )
    }


    override suspend fun delete(id: String) {
        TODO("Not yet implemented")
    }
}