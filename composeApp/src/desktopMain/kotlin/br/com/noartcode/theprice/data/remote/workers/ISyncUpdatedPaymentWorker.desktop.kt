package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.domain.repository.PaymentsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class SyncUpdatedPaymentWorker(
    private val paymentsRepository: PaymentsRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ISyncUpdatedPaymentWorker {

    override fun sync(paymentID: String) {
        CoroutineScope(ioDispatcher).launch {
            performSync(paymentID)
        }
    }

    private suspend fun performSync(paymentID: String, retriesLeft: Int = 5) {
        withContext(ioDispatcher) {
            val payment = paymentsRepository.get(paymentID)
            if (payment == null) {
                println("Payment not found")
                return@withContext
            }

            when (val result = paymentsRepository.put(payment)) {
                is Resource.Error -> {
                    println("Sync failed: ${result.exception?.message}")
                    if (retriesLeft > 0) {
                        delay(5000) // wait 5 seconds before retry
                        performSync(paymentID, retriesLeft - 1)
                    }
                }
                is Resource.Loading -> {
                    println("Received invalid loading state during sync")
                }
                is Resource.Success -> {
                    try {
                        paymentsRepository.update(payment.copy(isSynced = true))
                        println("Sync succeeded for $paymentID")
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}