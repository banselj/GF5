package com.example.gf5

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
        val response = vehicleService.updateVehicleLocation(vehicleId, latitude, longitude)
        if (response.isSuccessful) {
            return response.body() ?: false
        } else {
            throw Exception("Failed to update vehicle location: ${response.errorBody()?.string()}")
        }
    }
}
