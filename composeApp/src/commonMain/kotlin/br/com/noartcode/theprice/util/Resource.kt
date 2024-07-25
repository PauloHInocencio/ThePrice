package br.com.noartcode.theprice.util

sealed class Resource<out T> {
    class Success<out T>(val data:T) : Resource<T>()
    class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : Resource<Nothing>()
}

inline fun <reified A> Resource<A>.doIfSuccess(callback: (value: A) -> Unit): Resource<A> {
    (this as? Resource.Success)?.data?.let{ callback(it) }
    return this
}

inline fun <reified A> Resource<A>.doIfError(callback: (error: Resource.Error) -> Unit): Resource<A> {
    (this as? Resource.Error)?.let { callback(it) }
    return this
}