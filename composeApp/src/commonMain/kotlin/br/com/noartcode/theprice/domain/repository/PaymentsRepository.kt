package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {

   fun getMonthPayments(month:Int, year:Int) : Flow<List<Payment>>
   fun getBillPayments(billId:String): Flow<List<Payment>>
   suspend fun fetchAllPayments() : Resource<Unit>
   suspend fun getPayment(id:String) : Payment?
   suspend fun insert(payment: Payment)
   suspend fun insert(payments: List<Payment>)
   suspend fun delete(id:String)

}