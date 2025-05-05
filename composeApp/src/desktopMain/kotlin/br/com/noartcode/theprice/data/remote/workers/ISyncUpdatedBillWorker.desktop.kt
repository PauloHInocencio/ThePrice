package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class SyncUpdatedBillWorker(
    private val billsRepository: BillsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : ISyncUpdatedBillWorker {
    override fun sync(billID: String) {
        CoroutineScope(ioDispatcher).launch {
            performSync(billID)
        }
    }

    private suspend fun performSync(billID: String, retriesLeft:Int = 5) {
        withContext(ioDispatcher) {

            val bill = billsRepository.get(billID)
                ?: run {
                    println("Bill not found")
                    return@withContext
                }

            when(val result = billsRepository.put(bill)) {
                is Resource.Success -> {
                    try {
                        billsRepository.update(bill.copy(isSynced = true))
                        println("Successfully sync Bill: $billID")
                    } catch (e:Throwable) {
                        e.printStackTrace()
                        println("Error when trying update bill: ${e.message}")
                    }
                }
                is Resource.Error -> {
                    result.exception?.printStackTrace()
                    println("Sync failed: ${result.message}")
                    if (retriesLeft > 0) {
                        delay(5000) // wait 5 seconds before retry
                        performSync(billID,retriesLeft - 1)
                    }
                }
                is Resource.Loading -> {
                    println("Received invalid loading state during sync")
                }
            }
        }

    }
}