package com.example.gf5.repository

import com.example.gf5.network.ApiService
import javax.inject.Inject

class OnfidoRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : OnfidoRepository {

    override suspend fun fetchSdkToken(): String {
        val response = apiService.getOnfidoSdkToken()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return body.token
            } else {
                throw Exception("SDK token response body is null")
            }
        } else {
            throw Exception("Failed to fetch SDK token: ${response.errorBody()?.string()}")
        }
    }
}
