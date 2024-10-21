package com.example.gf5.network

data class RideRequest(
    val id: String,
    val riderId: String,
    val driverId: String?,
    val pickupLocation: String,
    val dropoffLocation: String,
    val status: String // e.g., "requested", "in_progress", "completed"
)