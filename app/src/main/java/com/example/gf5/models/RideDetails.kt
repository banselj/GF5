package com.example.gf5.models

import com.google.gson.annotations.SerializedName

data class RideDetails(
    @SerializedName("rideId") val rideId: String,
    @SerializedName("pickupLocation") val pickupLocation: String,
    @SerializedName("destinationLocation") val destinationLocation: String,
    @SerializedName("status") val status: String
)