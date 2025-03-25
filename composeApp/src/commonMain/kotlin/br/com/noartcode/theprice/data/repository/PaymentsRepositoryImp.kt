package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.datasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.data.remote.datasource.payment.PaymentRemoteDataSource
import br.com.noartcode.theprice.data.remote.mapper.toDomain
import br.com.noartcode.theprice.data.remote.mapper.toDto
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow

class PaymentsRepositoryImp(
    private val local: PaymentLocalDataSource,
    private val remote: PaymentRemoteDataSource,
) : PaymentsRepository {
    override fun getMonthPayments(month: Int, year: Int): Flow<List<Payment>> =
        local.getMonthPayments(month = month, year = year)

    override fun getBillPayments(billId: String): Flow<List<Payment>> {
        TODO("Not yet implemented")
    }

    override fun getAllPayments(): Flow<List<Payment>> =
        local.getAllPayments()


    override suspend fun fetchAllPayments(): Resource<Unit> {
        return when(val result = remote.fetchAllPayments()) {
            is Resource.Success -> {
                local.insert(result.data.toDomain())
                Resource.Success(Unit)
            }
            is Resource.Error -> result
            is Resource.Loading -> result
        }
    }

    override suspend fun getNotSynchronizedPayments(): List<Payment> {
        return local.getNotSynchronizedPayments()
    }

    override suspend fun getPayment(id: String): Payment? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payment: Payment) {
        local.insert(payment)
    }

    override suspend fun insert(payments: List<Payment>) {
        local.insert(payments)
    }

    override suspend fun post(payments: List<Payment>) : Resource<Unit> {
        return remote.post(payments.toDto())
    }

    override suspend fun update(payments: List<Payment>) {
        local.update(payments)
    }

    override suspend fun delete(id: String) {
        TODO("Not yet implemented")
    }


}