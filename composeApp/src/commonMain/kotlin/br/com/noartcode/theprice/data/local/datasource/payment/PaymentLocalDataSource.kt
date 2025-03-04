package br.com.noartcode.theprice.data.local.datasource.payment

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentLocalDataSource {
    fun getMonthPayments(month:Int, year:Int) : Flow<List<Payment>>
    suspend fun getBillPayments(billID:String) : List<Payment>
    suspend fun getPayment(billID:Long, month:Int, year:Int) : Payment?
    suspend fun getPayment(id:Long) : Payment?
    suspend fun updatePayment(id:Long, dueDate: DayMonthAndYear, price:Long, isPayed: Boolean)
    suspend fun insert(billID: String, dueDate: DayMonthAndYear, price:Long, isPayed:Boolean) : Long
    suspend fun insert(payments:List<Payment>)
    suspend fun delete(id:Long)
}