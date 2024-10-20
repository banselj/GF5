// File: com/example/gf5/network/ApiResponseHandler.kt
package com.example.gf5.network

import retrofit2.Response
import java.io.IOException

/**
 * Handles Retrofit API responses.
 *
 * @param T The type of the successful response body.
 * @param response The [Response] object from Retrofit.
 * @param errorMessage The error message to log or throw in case of failure.
 * @return The response body on success.
 * @throws IOException If a network error occurs.
 * @throws ApiException If the response is not successful or body is null.
 */
suspend fun <T> handleApiResponse(response: Response<T>, errorMessage: String): T {
    if (response.isSuccessful) {
        return response.body() ?: throw ApiException("Response body is null")
    } else {
        throw ApiException("$errorMessage: ${response.code()} ${response.message()}")
    }
}

/**
 * Custom exception for API-related errors.
 */
class ApiException(message: String) : Exception(message)
