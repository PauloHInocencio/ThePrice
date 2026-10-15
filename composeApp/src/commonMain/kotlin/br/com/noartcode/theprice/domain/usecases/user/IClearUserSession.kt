package br.com.noartcode.theprice.domain.usecases.user

import br.com.noartcode.theprice.data.local.queues.EventSyncQueue
import br.com.noartcode.theprice.domain.repository.AuthRepository
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface IClearUserSession {
    suspend operator fun invoke()
}

class ClearUserSession(
    private val authRepository: AuthRepository,
    private val billsRepository: BillsRepository,
    private val paymentsRepository: PaymentsRepository,
    private val eventSyncQueue: EventSyncQueue,
    private val dispatcher: CoroutineDispatcher,
) : IClearUserSession {
    override suspend fun invoke() = withContext(dispatcher) {
        authRepository.clearSession()
        eventSyncQueue.clean()
        billsRepository.clean()
        paymentsRepository.clean()
    }
}
