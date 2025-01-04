package com.example.gf5.repository


import com.example.gf5.models.Ad

interface AdRepository {
    suspend fun getAllAds(): List<Ad>
    // Define other ad-related operations as needed
}
