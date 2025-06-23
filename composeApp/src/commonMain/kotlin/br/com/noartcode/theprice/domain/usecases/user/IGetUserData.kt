package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IGetUserData {
    suspend operator fun invoke() : Resource<Unit>
}

class GetUserData(
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : IGetUserData {
    override suspend fun invoke() = withContext(ioDispatcher)  {
        return@withContext when(val billResult = billsRepository.fetchAllBills()) {
            is Resource.Error ->  {
                billResult
            }
            else -> paymentsRepository.fetchAllPayments()
        }
    }
}