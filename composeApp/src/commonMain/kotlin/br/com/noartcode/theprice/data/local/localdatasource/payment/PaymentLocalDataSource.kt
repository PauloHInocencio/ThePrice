package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentLocalDataSource {
    suspend fun getMonthPayments(month:Int, year:Int) : List<Payment>
    suspend fun getBillPayments(billID:Long) : List<Payment>
    suspend fun getPayment(billID:Long, month:Int, year:Int) : Payment?
    suspend fun getPayment(id:Long) : Payment?
    suspend fun updatePayment(id:Long, paidValue:Int?, paidAt: DayMonthAndYear?) : Payment?
    suspend fun insert(
        billID: Long,
        dueDate: DayMonthAndYear,
        paidValue: Int? = null,
        paidAt: DayMonthAndYear? = null
    ) : Long
    suspend fun delete(id:Long)
}