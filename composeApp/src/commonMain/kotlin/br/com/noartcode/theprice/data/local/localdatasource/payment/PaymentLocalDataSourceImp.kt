package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.data.local.ThePrinceDatabase
import br.com.noartcode.theprice.data.local.entities.PaymentEntity
import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.domain.usecases.IGetDateFormat
import kotlinx.coroutines.flow.Flow

class PaymentLocalDataSourceImp (
    private val database: ThePrinceDatabase,
    private val dateFormat: IGetDateFormat
): PaymentLocalDataSource {

    private val dao by lazy { database.getPaymentDao() }
    private val billDao by lazy { database.getBillDao() }
    override suspend fun getMonthPayments(month: Int, year: Int): List<Payment> {
        TODO("Not yet implemented")
    }

    override suspend fun getBillPayments(billID: Long): List<Payment> {
        val billName = billDao.getBill(id = billID)!!.name
        return dao
            .getBillPayments(billID)
            .map {
                Payment(
                    id = it.id,
                    billId = billID,
                    billTitle = billName,
                    dueDate = dateFormat(it.day, it.month, it.year),
                    paidValue = it.paidValue,
                    paidAt = it.paidAt
                )
            }
    }

    override suspend fun getPayment(billID: Long, month: Int, year: Int): Payment? {
        val billName = billDao.getBill(id = billID)!!.name
        return dao
            .getPayment(billId = billID, month = month, year = year)
            ?.let {
                Payment(
                    id = it.id,
                    billId = billID,
                    billTitle = billName,
                    dueDate = dateFormat(it.day, it.month, it.year),
                    paidValue = it.paidValue,
                    paidAt = it.paidAt
                )
            }
    }

    override suspend fun getPayment(id: Long, billID: Long): Payment? {
        val billName = billDao.getBill(id = billID)!!.name
        return dao
            .getPayment(id)?.let {
                Payment(
                    id = it.id,
                    billId = billID,
                    billTitle = billName,
                    dueDate = dateFormat(it.day, it.month, it.year),
                    paidValue = it.paidValue,
                    paidAt = it.paidAt
                )
            }
    }

    override suspend fun insert(
        billID: Long,
        day: Int,
        month: Int,
        year: Int,
        paidValue: Int?,
        paidAt: String?
    ) : Long {
        return dao.insert(
            PaymentEntity(
                billId = billID,
                day = day,
                month = month,
                year = year,
                paidValue = paidValue,
                paidAt = paidAt
            )
        )
    }



    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }
}