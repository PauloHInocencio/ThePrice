package br.com.noartcode.theprice.data.remote.workers

interface ISyncUpdatedBillWorker {
    operator fun invoke(billID:String)
}

expect class SyncUpdatedBillWorker : ISyncUpdatedBillWorker
