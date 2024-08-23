package br.com.noartcode.theprice.data.localdatasource.helpers

import br.com.noartcode.theprice.data.local.localdatasource.payment.PaymentLocalDataSource
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment

class PaymentLocalDataSourceFakeImp : PaymentLocalDataSource {
    private val store = mutableListOf<Payment>()
    override suspend fun getMonthPayments(month: Int, year: Int): List<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun getBillPayments(billID: Long): List<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(billID: Long, month: Int, year: Int): Payment? {
        TODO("Not yet implemented")
    }

    override suspend fun getPayment(id: Long, billID: Long): Payment? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(
        billID: Long,
        dueDate: DayMonthAndYear,
        paidValue: Int?,
        paidAt: DayMonthAndYear?
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }


}