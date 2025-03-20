package br.com.noartcode.theprice.data.remote.workers

interface ISyncPaymentsWorker {
    operator fun invoke()
}

expect class SyncPaymentsWorker : ISyncPaymentsWorker