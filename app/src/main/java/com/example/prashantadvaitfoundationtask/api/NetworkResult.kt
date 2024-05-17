package com.example.prashantadvaitfoundationtask.api

sealed class NetworkResult<out T : Any> {
    object Loading : NetworkResult<Nothing>()
    data class Success<out T : Any>(val code: Int, val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val errorMsg: String?) : NetworkResult<String>()
    data class Exception(val e: Throwable) : NetworkResult<Nothing>()
}