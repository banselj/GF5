package com.example.gf5.models

import com.google.gson.annotations.SerializedName

data class Driver(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val carModel: String,
    val licensePlate: String
)
