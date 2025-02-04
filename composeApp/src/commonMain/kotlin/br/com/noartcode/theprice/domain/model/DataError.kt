package br.com.noartcode.theprice.domain.model

sealed class DataError: Error() {
    data object RequestTimeOut : DataError()
    data object Unauthorized: DataError()
    data object Conflict: DataError()
    data object ServerError: DataError()
    data object NoIntent: DataError()
    data object Serialization: DataError()
    data object Unknown: DataError()
}