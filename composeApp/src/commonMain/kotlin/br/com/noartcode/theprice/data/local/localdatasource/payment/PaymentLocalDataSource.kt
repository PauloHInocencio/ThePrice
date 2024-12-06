package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.domain.model.DayMonthAndYear
import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentLocalDataSource {
    fun getMonthPayments(month:Int, year:Int) : Flow<List<Payment>>
    suspend fun getBillPayments(billID:Long) : List<Payment>
    suspend fun getPayment(billID:Long, month:Int, year:Int) : Payment?
    suspend fun getPayment(id:Long) : Payment?
    suspend fun updatePayment(id:Long, dueDate: DayMonthAndYear, price:Long, isPayed: Boolean)
    suspend fun insert(billID: Long, dueDate: DayMonthAndYear, price:Long, isPayed:Boolean) : Long
    suspend fun delete(id:Long)
}