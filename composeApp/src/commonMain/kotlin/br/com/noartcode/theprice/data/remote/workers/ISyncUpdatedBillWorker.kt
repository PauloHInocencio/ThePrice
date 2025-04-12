package br.com.noartcode.theprice.data.remote.workers

interface ISyncUpdatedBillWorker {
    fun sync(billID:String)
}

expect class SyncUpdatedBillWorker : ISyncUpdatedBillWorker
