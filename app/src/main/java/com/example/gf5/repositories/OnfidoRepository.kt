// File: com/example/gf5/repository/OnfidoRepository.kt
package com.example.gf5.repositories

import javax.inject.Inject

class OnfidoRepository @Inject constructor(
    private val apiService: ApiService // Retrofit API service
) {
    /**
     * Fetches the Onfido SDK token from the backend.
     */
    suspend fun fetchSdkToken(): String {
        val response = apiService.getOnfidoSdkToken()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.token
        } else {
            throw Exception("Failed to fetch SDK token: ${response.errorBody()?.string()}")
        }
    }
}
