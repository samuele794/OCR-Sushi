package it.github.samuele794.sushisigner.utils

import com.github.kittinunf.result.coroutines.SuspendableResult

suspend fun <V, E : Exception> SuspendableResult<V, E>.onFailure(action: suspend (Throwable) -> Unit): SuspendableResult<V, E> {
    this.fold({}, { action.invoke(it) })
    return this
}

suspend fun <V, E : Exception> SuspendableResult<V, E>.onSuccess(action: suspend (value: V) -> Unit): SuspendableResult<V, E> {
    this.fold({ action.invoke(it) }, {})
    return this
}