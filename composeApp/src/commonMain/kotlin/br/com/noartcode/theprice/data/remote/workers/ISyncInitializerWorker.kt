package br.com.noartcode.theprice.data.remote.workers

interface ISyncInitializerWorker {
    operator fun invoke()
}

expect class SyncInitializerWorker : ISyncInitializerWorker