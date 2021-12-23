package com.example.volumen.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline databaseQuery: () -> Flow<ResultType>,
    crossinline networkCall: suspend () -> RequestType,
    crossinline saveCallResult: suspend (RequestType) -> Unit,
    crossinline shouldNetworkCall: (ResultType) -> Boolean = { true }
) = flow {

    val data = databaseQuery().first()

    val flow = if (shouldNetworkCall(data)) {
        emit(Resource.Loading(data))

        try {
            saveCallResult(networkCall())
            databaseQuery().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            databaseQuery().map {
                Resource.Error(throwable = throwable, data = it)
            }
        }

    } else {
        databaseQuery().map { Resource.Success(it) }
    }

    emitAll(flow)

}