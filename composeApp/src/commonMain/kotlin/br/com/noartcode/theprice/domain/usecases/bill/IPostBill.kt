package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IPostBill {
    suspend operator fun invoke(bill:Bill) : Resource<Unit>
}

class PostBill(
    private val repository: BillsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : IPostBill{
    override suspend fun invoke(bill: Bill): Resource<Unit> = withContext(ioDispatcher) {
       return@withContext when(val result = repository.post(bill)) {
           is Resource.Success-> {
               try {
                   repository.update(bill.copy(isSynced = true))
                   result
               } catch (e:Throwable) {
                   e.printStackTrace()
                   Resource.Error(message = "Error when trying update bill", exception = e)
               }
           }
           is Resource.Error -> {
               result.exception?.printStackTrace()
               result
           }
           is Resource.Loading -> {
               Resource.Error(message = "Invalid response while posting bill")
           }
       }
    }
}