package br.com.noartcode.theprice.data.remote.workers

interface ISyncUpdatedPaymentWorker {
    fun sync(paymentID:String)
}

expect class SyncUpdatedPaymentWorker : ISyncUpdatedPaymentWorker