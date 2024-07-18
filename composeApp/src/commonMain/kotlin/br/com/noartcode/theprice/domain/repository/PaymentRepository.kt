package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {

   fun getMonthPayments(month:Int, year:Int) : Flow<Resource<List<Payment>>>

   fun getExpensePayments(expenseId:Int) : Flow<Resource<List<Payment>>>

   suspend fun getPayment(expenseId:Int, date:String) : Resource<Payment>

   suspend fun insert(payment: Payment)

   suspend fun delete(id:Int)

}