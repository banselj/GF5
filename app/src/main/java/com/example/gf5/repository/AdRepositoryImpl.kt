package com.example.gf5.repository


import com.example.gf5.network.AdService
import com.example.gf5.models.Ad
import javax.inject.Inject

class AdRepositoryImpl @Inject constructor(
    private val adService: AdService
) : AdRepository {

    override suspend fun getAllAds(): List<Ad> {
        val response = adService.getAllAds()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch ads: ${response.errorBody()?.string()}")
        }
    }

    // Implement other ad-related operations
}
