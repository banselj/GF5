package com.example.gf5

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Query

data class LoginRequest(
    val email: String,
    val password: String
)

data class RideRequest(
    val pickupLocation: String,
    val destinationLocation: String
)

data class UserResponse(
    val userId: String,
    val userName: String,
    val userEmail: String
    // Add other fields as per your API response structure
)

data class DriverLocationResponse(
    val latitude: Double,
    val longitude: Double,
    // Include additional fields as necessary
)

data class RideResponse(
    val rideId: String,
    val status: String,
    val pickupLocation: String,
    val destinationLocation: String,
    // Add any other relevant fields
)

data class Ride(
    val rideId: String,
    val pickupLocation: String,
    val destinationLocation: String,
    val status: String
    // Add additional properties as needed.
)

interface ApiService {
    @POST("users/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<UserResponse>

    @POST("rides/request")
    suspend fun requestRide(@Body rideRequest: RideRequest): Response<RideResponse>

    @GET("drivers/{driverId}/location")
    fun getDriverLocation(@Path("driverId") driverId: String): Call<DriverLocationResponse>

    @GET("rides/search")
    fun searchRides(@Query("pickupLocation") pickupLocation: String, @Query("destinationLocation") destinationLocation: String): Call<List<Ride>>
}
