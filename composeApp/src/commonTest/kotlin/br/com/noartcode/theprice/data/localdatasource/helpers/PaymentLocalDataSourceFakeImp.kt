package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

class PaymentLocalDataSourceFakeImp : PaymentLocalDataSource {
    private val store = mutableListOf<Payment>()
    override fun getMonthPayments(month: Int, year: Int): Flow<List<Payment>> {
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

    override suspend fun updatePayment(
        id: Long,
        dueDate: DayMonthAndYear,
        price: Long,
        isPayed: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(
        billID: Long,
        dueDate: DayMonthAndYear,
        price: Long,
        isPayed: Boolean
    ): Long {
        TODO("Not yet implemented")
    }


    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }


}