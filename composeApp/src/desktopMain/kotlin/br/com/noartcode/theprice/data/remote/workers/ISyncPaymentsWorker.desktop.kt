package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class SyncPaymentsWorker(
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ISyncPaymentsWorker {

    override fun sync() {
        CoroutineScope(ioDispatcher).launch {
            performSync()
        }
    }

    private suspend fun performSync(retriesLeft:Int = 5) {
        withContext(ioDispatcher){
            val paymentsToSync = paymentsRepository.getNotSynchronizedPayments()

            if (paymentsToSync.isEmpty()) {
                println("No payments to sync")
                return@withContext
            }

            when(val result = paymentsRepository.post(paymentsToSync)) {
                is Resource.Success ->  {
                    try {
                        paymentsRepository.update(paymentsToSync.map{ it.copy(isSynced = true) })
                        println("Successfully sync payments")
                    } catch (e:Throwable) {
                        e.printStackTrace()
                        println("Error when trying update payments: ${e.message}")
                    }
                }
                is Resource.Error -> {
                    result.exception?.printStackTrace()
                    println("Sync failed: ${result.message}")
                    if (retriesLeft > 0) {
                        delay(5000) // wait 5 seconds before retry
                        performSync(retriesLeft - 1)
                    }
                }
                is Resource.Loading -> {
                    println("Received invalid loading state during sync")
                }
            }
        }
    }
}