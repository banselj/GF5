package com.example.gf5.repositories

import com.example.gf5.models.Vehicle

interface VehicleRepository {
    suspend fun getAvailableVehicles(): List<Vehicle>
    suspend fun updateVehicleLocation(vehicleId: String, latitude: Double, longitude: Double): Boolean
}
