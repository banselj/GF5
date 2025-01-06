package com.example.gf5.models

import com.google.gson.annotations.SerializedName

data class Driver(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("rating") val rating: Float,
    @SerializedName("carModel") val carModel: String,
    @SerializedName("licensePlate") val licensePlate: String
)