package com.example.gf5

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DriverService {
    @GET("drivers/available")
    suspend fun getAvailableDrivers(): Response<List<Driver>>

    @GET("drivers/{driverId}")
    suspend fun getDriverDetails(@Path("driverId") driverId: String): Response<Driver>

    @POST("drivers/{driverId}/status")
    suspend fun updateDriverStatus(@Path("driverId") driverId: String, @Body newStatus: DriverStatus): Response<Boolean>

    @GET("drivers/{driverId}/next-ride")
    suspend fun getNextRide(@Path("driverId") driverId: String): Response<RideDetails>
}
