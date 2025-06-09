package br.com.noartcode.theprice.data.remote.workers

interface ISyncDeletedBillWorker {
    fun sync(billID:String)
}

expect class SyncDeletedBillWorker: ISyncDeletedBillWorker