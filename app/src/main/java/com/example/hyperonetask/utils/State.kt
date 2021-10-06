package com.example.hyperonetask.utils

sealed class State<T> {

    class Loading<T>(): State<T>()
    data class Success<T>(val data: T): State<T>()
    data class Error<T>(val msg: String? = null): State<T>()
}
