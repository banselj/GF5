package com.example.gf5.models

data class Vehicle(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val status: String // e.g., "available", "unavailable"
)
