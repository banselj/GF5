package com.example.gf5

interface VehicleRepository {
    suspend fun getAvailableVehicles(): List<Vehicle>
    suspend fun updateVehicleLocation(vehicleId: String, latitude: Double, longitude: Double): Boolean
}
