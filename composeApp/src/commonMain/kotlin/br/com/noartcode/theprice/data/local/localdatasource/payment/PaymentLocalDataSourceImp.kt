package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.data.local.mapper.toDomain
import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment

class PaymentLocalDataSourceImp (
    private val database: ThePrinceDatabase
): PaymentLocalDataSource {

    private val dao by lazy { database.getPaymentDao() }
    override suspend fun getMonthPayments(month: Int, year: Int): List<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun getBillPayments(billID: Long): List<Payment> {
        return dao
            .getBillPayments(billID)
            .map {
                it.toDomain(billID)
            }
    }

    override suspend fun getPayment(billID: Long, month: Int, year: Int): Payment? {
        return dao
            .getPayment(billId = billID, month = month, year = year)
            ?.toDomain(billID)
    }

    override suspend fun getPayment(id: Long, billID: Long): Payment? {
        return dao.getPayment(id)?.toDomain(billID)
    }

    override suspend fun insert(
        billID: Long,
        dueDate: DayMonthAndYear,
        paidValue: Int?,
        paidAt: DayMonthAndYear?
    ) : Long {
        return dao.insert(
            PaymentEntity(
                billId = billID,
                dueDay = dueDate.day,
                dueMonth = dueDate.month,
                dueYear = dueDate.year,
                paidValue = paidValue,
                paidDay = paidAt?.day,
                paidMonth = paidAt?.month,
                paidYear = paidAt?.year

            )
        )
    }



    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}