package br.com.noartcode.theprice.util

sealed class Resource<out T> {
    class Success<out T>(val data:T) : Resource<T>()
    class Error(
        val message: String,
        val code: Int? = null,
        val exception: Throwable? = null
    ) : Resource<Nothing>()
    data object Loading: Resource<Nothing>()

    override fun toString(): String = when(this) {
        is Success -> "Success(data=$data)"
        is Error -> "Error(message=$message)"
        is Loading -> "Loading"
    }
}

inline fun <reified A> Resource<A>.doIfSuccess(callback: (value: A) -> Unit): Resource<A> {
    (this as? Resource.Success)?.data?.let{ callback(it) }
    return this
}

inline fun <reified A> Resource<A>.doIfError(callback: (error: Resource.Error) -> Unit): Resource<A> {
    (this as? Resource.Error)?.let { callback(it) }
    return this
}

inline fun <A, B> Resource<A>.map(transform: (A) -> B): Resource<B> {
    return when (this) {
        is Resource.Success -> Resource.Success(transform(this.data))
        is Resource.Error -> this
        is Resource.Loading -> this
    }
}