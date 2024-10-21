package com.example.gf5.network


data class RideResponse(
    val rideId: String,
    val status: String,
    val pickupLocation: String,
    val destinationLocation: String
)
