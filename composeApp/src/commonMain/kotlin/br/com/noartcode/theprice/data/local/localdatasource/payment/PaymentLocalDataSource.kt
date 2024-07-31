package br.com.noartcode.theprice.data.local.localdatasource.payment

import br.com.noartcode.theprice.domain.model.Payment
import kotlinx.coroutines.flow.Flow

interface PaymentLocalDataSource {
    fun getMonthPayments(month:Int, year:Int) : Flow<List<Payment>>
    fun getExpensePayments(expenseId:Int) : Flow<List<Payment>>
    suspend fun getPayment(expenseId:Int, date:String) : Payment
    suspend fun insert(payment: Payment)
    suspend fun delete(id:Int)
}