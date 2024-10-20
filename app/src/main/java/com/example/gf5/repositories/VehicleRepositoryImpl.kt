package com.example.gf5.repositories

import com.example.gf5.models.Vehicle
import com.example.gf5.network.VehicleService

class VehicleRepositoryImpl(private val vehicleService: VehicleService) : VehicleRepository {
    override suspend fun getAvailableVehicles(): List<Vehicle> {
        val response = vehicleService.getAvailableVehicles()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to get available vehicles: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun updateVehicleLocation(vehicleId: String, latitude: Double, longitude: Double): Boolean {
        // Create a map for the location
        val location = mapOf(
            "latitude" to latitude,
            "longitude" to longitude
        )

        val response = vehicleService.updateVehicleLocation(vehicleId, location)
        if (response.isSuccessful) {
            return response.body() ?: false
        } else {
            throw Exception("Failed to update vehicle location: ${response.errorBody()?.string()}")
        }
    }
}
