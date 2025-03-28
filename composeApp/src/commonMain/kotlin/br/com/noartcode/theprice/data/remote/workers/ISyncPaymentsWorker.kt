package br.com.noartcode.theprice.data.remote.workers

interface ISyncPaymentsWorker {
     fun sync()
}

expect class SyncPaymentsWorker : ISyncPaymentsWorker