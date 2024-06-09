package com.example.gf5

import java.sql.Driver

interface DriverRepository {
    suspend fun getAvailableDrivers(): List<Driver>
    suspend fun getDriverDetails(driverId: String): Driver
    suspend fun <DriverStatus> updateDriverStatus(driverId: String, newStatus: DriverStatus): Boolean
}

class DriverRepositoryImpl(private val driverService: DriverService) : DriverRepository {
    override suspend fun getAvailableDrivers(): List<Driver> {
        val response = driverService.getAvailableDrivers()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to get available drivers: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun getDriverDetails(driverId: String): Driver {
        val response = driverService.getDriverDetails(driverId)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Driver not found")
        } else {
            throw Exception("Failed to get driver details: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun updateDriverStatus(driverId: String, newStatus: DriverStatus): Boolean {
        val response = driverService.updateDriverStatus(driverId, newStatus)
        if (response.isSuccessful) {
            return response.body() ?: false
        } else {
            throw Exception("Failed to update driver status: ${response.errorBody()?.string()}")
        }
    }
}
