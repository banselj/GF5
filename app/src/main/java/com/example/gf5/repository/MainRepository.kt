package com.example.gf5.repository

import com.example.gf5.network.ApiService

class MainRepository(private val apiService: ApiService) {
    suspend fun fetchData() = apiService.fetchData()
    suspend fun sendData(data: YourRequestModel) = apiService.sendData(data)
}
