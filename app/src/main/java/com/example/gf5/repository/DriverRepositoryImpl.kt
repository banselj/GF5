
package com.example.gf5.repository

import com.example.gf5.models.Driver
import com.example.gf5.models.DriverStatus
import com.example.gf5.models.RideDetails
import com.example.gf5.network.DriverService
import com.example.gf5.network.handleApiResponse
import javax.inject.Inject

/**
 * Implementation of [DriverRepository] that interacts with [DriverService].
 */
class DriverRepositoryImpl @Inject constructor(
    private val driverService: DriverService
) : DriverRepository {

    override suspend fun getAvailableDrivers(): List<Driver> {
        val response = driverService.getAvailableDrivers()
        return handleApiResponse(response, "Failed to retrieve available drivers")
    }

    override suspend fun getDriverDetails(driverId: String): Driver {
        val response = driverService.getDriverDetails(driverId)
        return handleApiResponse(response, "Driver not found with ID: $driverId")
    }

    override suspend fun updateDriverStatus(driverId: String, newStatus: DriverStatus): Boolean {
        val response = driverService.updateDriverStatus(driverId, newStatus)
        // Assuming the API returns a Boolean in the response body
        // If the API returns a different structure, adjust accordingly
        return handleApiResponse(response, "Failed to update driver status for ID: $driverId")
    }

    override suspend fun getNextRide(driverId: String): RideDetails {
        val response = driverService.getNextRide(driverId)
        return handleApiResponse(response, "No next ride found for driver ID: $driverId")
    }

    override suspend fun logoutDriver() {
        // Implement logout logic, e.g., clear user data, inform server, etc.
        driverService.logoutDriver()
    }
}
