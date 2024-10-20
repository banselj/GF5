package com.example.gf5.repositories

import com.example.gf5.models.Driver
import com.example.gf5.models.RideDetails
import com.example.gf5.models.DriverStatus

/**
 * Repository interface for driver-related operations.
 */
interface DriverRepository {
    /**
     * Retrieves a list of available drivers.
     *
     * @return A list of [Driver] objects.
     */
    suspend fun getAvailableDrivers(): List<Driver>

    /**
     * Retrieves details of a specific driver by ID.
     *
     * @param driverId The unique identifier of the driver.
     * @return The [Driver] object corresponding to the provided ID.
     */
    suspend fun getDriverDetails(driverId: String): Driver

    /**
     * Updates the status of a driver.
     *
     * @param driverId The unique identifier of the driver.
     * @param newStatus The new status to set for the driver.
     * @return `true` if the update was successful, `false` otherwise.
     */
    suspend fun updateDriverStatus(driverId: String, newStatus: DriverStatus): Boolean

    /**
     * Retrieves the next ride assigned to a driver.
     *
     * @param driverId The unique identifier of the driver.
     * @return The [RideDetails] object for the next ride.
     */
    suspend fun getNextRide(driverId: String): RideDetails

    /**
     * Logs out the current driver.
     *
     * @return `true` if logout was successful, `false` otherwise.
     */
    suspend fun logoutDriver()
}
