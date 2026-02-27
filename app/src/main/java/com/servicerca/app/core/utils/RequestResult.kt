package com.servicerca.app.core.utils

sealed class RequestResult {
    data class Success (val message: String): RequestResult()
    data class Failure (val errorMessage: String): RequestResult()
}