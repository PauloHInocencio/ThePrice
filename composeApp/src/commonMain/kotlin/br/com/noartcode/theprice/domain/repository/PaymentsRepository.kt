package br.com.noartcode.theprice.domain.repository

import br.com.noartcode.theprice.domain.model.Payment
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentsRepository {
   fun getMonthPayments(month:Int, year:Int) : Flow<List<Payment>>
   fun getBillPayments(billId:String): Flow<List<Payment>>
   fun getAllPayments() : Flow<List<Payment>>
   suspend fun fetchAllPayments() : Resource<Unit>
   suspend fun getNotSynchronizedPayments() : List<Payment>
   suspend fun get(id:String) : Payment?
   suspend fun insert(payment: Payment) : String
   suspend fun insert(payments: List<Payment>)
   suspend fun put(payment: Payment) : Resource<Unit>
   suspend fun post(payments: List<Payment>) : Resource<Unit>
   suspend fun update(payments: List<Payment>)
   suspend fun update(payment: Payment)
   suspend fun delete(id:String)
}