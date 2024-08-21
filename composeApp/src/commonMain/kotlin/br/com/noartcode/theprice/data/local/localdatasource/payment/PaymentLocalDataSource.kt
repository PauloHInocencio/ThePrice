package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentLocalDataSource {
    suspend fun getMonthPayments(month:Int, year:Int) : List<Payment>
    suspend fun getBillPayments(billID:Long) : List<Payment>
    suspend fun getPayment(billID:Long, month:Int, year:Int) : Payment?
    suspend fun getPayment(id:Long, billID:Long) : Payment?
    suspend fun insert(
        billID:Long,
        day:Int,
        month:Int,
        year:Int,
        paidValue:Int? = null,
        paidAt:String? = null
    ) : Long
    suspend fun delete(id:Long)
}