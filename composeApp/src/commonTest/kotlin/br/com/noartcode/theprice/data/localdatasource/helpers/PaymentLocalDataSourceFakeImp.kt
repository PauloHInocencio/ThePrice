package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

class PaymentLocalDataSourceFakeImp : PaymentLocalDataSource {
    override suspend fun getMonthPayments(month: Int, year: Int): List<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun getBillPayments(billID: Long): List<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(billID: Long, month: Int, year: Int): Payment? {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(id: Long): Payment? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payment: Payment): Long {
        TODO("Not yet implemented")
    }


    override suspend fun delete(id: Int) {
        TODO("Not yet implemented")
    }
}