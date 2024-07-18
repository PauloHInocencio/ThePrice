package br.com.noartcode.theprice.data.repository

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.dao.PaymentDao
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.repository.PaymentRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow

class PaymentRepositoryImp(
    private val database: ThePrinceDatabase
) : PaymentRepository {

    private val dao: PaymentDao by lazy {
        database.getPaymentDao()
    }

    override fun getMonthPayments(month: Int, year: Int): Flow<Resource<List<Payment>>> {
        TODO("Not yet implemented")
    }

    override fun getExpensePayments(expenseId: Int): Flow<Resource<List<Payment>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(
        expenseId: Int,
        date:String
    ): Resource<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payment: Payment) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}