package com.example.gf5.network


import retrofit2.Response
import java.io.IOException
import retrofit2.HttpException

suspend fun <T> handleApiResponse(response: Response<T>, notFoundMessage: String = "Data not found"): T {
    if (response.isSuccessful) {
        return response.body() ?: throw ApiException.DataNotFound(notFoundMessage)
    } else {
        when (response.code()) {
            404 -> throw ApiException.DataNotFound(notFoundMessage)
            in 500..599 -> throw ApiException.ServerError("Server error: ${response.code()} ${response.message()}", response.code())
            else -> throw ApiException.UnknownError("Unknown error: ${response.code()} ${response.message()}")
        }
    }
}
