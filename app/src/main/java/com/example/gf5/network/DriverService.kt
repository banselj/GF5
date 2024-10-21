
package com.example.gf5.network

import com.example.gf5.models.Driver
import com.example.gf5.models.DriverStatus
import com.example.gf5.models.RideDetails
import retrofit2.Response
import retrofit2.http.*

interface DriverService {

    /**
     * Retrieves a list of available drivers.
     */
    @GET("drivers/available")
    suspend fun getAvailableDrivers(): Response<List<Driver>>

    /**
     * Retrieves details of a specific driver by ID.
     *
     * @param driverId The unique identifier of the driver.
     */
    @GET("drivers/{id}")
    suspend fun getDriverDetails(@Path("id") driverId: String): Response<Driver>

    /**
     * Updates the status of a driver.
     *
     * @param driverId The unique identifier of the driver.
     * @param status The new status to set for the driver.
     */
    @PUT("drivers/{id}/status")
    suspend fun updateDriverStatus(
        @Path("id") driverId: String,
        @Body status: DriverStatus
    ): Response<Boolean> // Ensure the API returns a Boolean

    /**
     * Retrieves the next ride assigned to a driver.
     *
     * @param driverId The unique identifier of the driver.
     */
    @GET("drivers/{id}/nextRide")
    suspend fun getNextRide(@Path("id") driverId: String): Response<RideDetails>

    /**
     * Logs out the current driver.
     */
    @POST("drivers/logout")
    suspend fun logoutDriver(): Response<Unit>
}
