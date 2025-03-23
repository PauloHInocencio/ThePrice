package br.com.noartcode.theprice.data.local.datasource.payment

import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentLocalDataSource {
    fun getMonthPayments(month:Int, year:Int) : Flow<List<Payment>>
    suspend fun getBillPayments(billID:String) : List<Payment>
    suspend fun getNotSynchronizedPayments() : List<Payment>
    suspend fun getPayment(billID:String, month:Int, year:Int) : Payment?
    suspend fun getPayment(id:String) : Payment?
    suspend fun update(payment: Payment)
    suspend fun update(payments: List<Payment>)
    suspend fun insert(payment: Payment) : String
    suspend fun insert(payments:List<Payment>)
    suspend fun delete(id:String)
}