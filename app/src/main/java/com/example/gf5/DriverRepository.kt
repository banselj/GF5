package com.example.gf5

interface DriverRepository {
    suspend fun getAvailableDrivers(): List<Driver>
    suspend fun getDriverDetails(driverId: String): Driver
    suspend fun updateDriverStatus(driverId: String, newStatus: DriverStatus): Boolean
    suspend fun getNextRide(driverId: String): RideDetails
}
