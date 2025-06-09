package br.com.noartcode.theprice.data.remote.workers

import br.com.noartcode.theprice.data.remote.datasource.bill.BillRemoteDataSource
import br.com.noartcode.theprice.domain.repository.BillsRepository
import br.com.noartcode.theprice.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

actual class SyncDeletedBillWorker(
    private val remoteDataSource: BillRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : ISyncDeletedBillWorker {
    override fun sync(billID: String) {
        CoroutineScope(ioDispatcher).launch {
            performSync(billID)
        }
    }

    private suspend fun performSync(billID: String, retriesLeft:Int = 5) {
        withContext(ioDispatcher) {
            when(val result = remoteDataSource.delete(billID)) {
                is Resource.Success -> {
                    println("Successfully sync deleted Bill: $billID")
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