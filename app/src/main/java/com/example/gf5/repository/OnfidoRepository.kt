package com.example.gf5.repository

import com.example.gf5.network.ApiService
import javax.inject.Inject

open class OnfidoRepository @Inject constructor(
    private val apiService: ApiService
) {
    constructor() : this()

    open suspend fun fetchSdkToken(): String {
        val response = apiService.getOnfidoSdkToken()
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!.token
        } else {
            throw Exception("Failed to fetch SDK token: ${response.errorBody()?.string()}")
        }
    }
}