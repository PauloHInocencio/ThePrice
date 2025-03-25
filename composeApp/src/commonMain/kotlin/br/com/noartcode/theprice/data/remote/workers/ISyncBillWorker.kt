package br.com.noartcode.theprice.data.remote.workers


interface ISyncBillWorker {
    fun sync(billID:String)
}

expect class  SyncBillWorker :  ISyncBillWorker