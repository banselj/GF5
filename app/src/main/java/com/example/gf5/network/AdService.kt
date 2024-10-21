package com.example.gf5.network


import com.example.gf5.models.Ad
import retrofit2.Response
import retrofit2.http.GET

interface AdService {
    @GET("ads")
    suspend fun getAllAds(): Response<List<Ad>>
}
