package br.com.noartcode.theprice.domain.usecases.bill

import br.com.noartcode.theprice.domain.model.Bill
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IDeleteRemoteBill {
    suspend operator fun invoke(bill: Bill) : Resource<Unit>
}

class DeleteRemoteBill(
    private val repository: BillsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : IDeleteRemoteBill {
    override suspend fun invoke(bill: Bill): Resource<Unit> = withContext(ioDispatcher) {
      return@withContext when(val result = repository.deleteRemote(bill)) {
          is Resource.Success-> result
          is Resource.Loading -> Resource.Error("Invalid response while deleting bill")
          is Resource.Error -> {
              result.exception?.printStackTrace()
              result
          }
      }
    }

}