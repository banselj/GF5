package com.example.gf5.network

sealed class ApiException(message: String) : Exception(message) {
    class NetworkError(message: String) : ApiException(message)
    class ServerError(message: String, val code: Int) : ApiException(message)
    class DataNotFound(message: String) : ApiException(message)
    class UnknownError(message: String) : ApiException(message)
}
