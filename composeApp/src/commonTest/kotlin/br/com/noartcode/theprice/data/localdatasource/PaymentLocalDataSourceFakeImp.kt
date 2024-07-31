package br.com.noartcode.theprice.data.localdatasource

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

class PaymentLocalDataSourceFakeImp : PaymentLocalDataSource {

    override fun getMonthPayments(month: Int, year: Int): Flow<List<Payment>> {
        TODO("Not yet implemented")
    }

    override fun getExpensePayments(expenseId: Int): Flow<List<Payment>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(expenseId: Int, date: String): Payment {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payment: Payment) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}