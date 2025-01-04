
package com.example.gf5.network


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Represents a request to log in a user.
 *
 * @property email The user's email address.
 * @property password The user's password.
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Represents a request to initiate a ride.
 *
 * @property pickupLocation The location where the rider will be picked up.
 * @property destinationLocation The rider's destination.
 */
data class RideInitiateRequest(
    val pickupLocation: String,
    val destinationLocation: String
)

/**
 * Represents the response received after a user logs in.
 *
 * @property userId The unique identifier of the user.
 * @property userName The name of the user.
 * @property userEmail The email address of the user.
 * @property phoneNumber The user's phone number (nullable).
 * @property role The role of the user (e.g., "rider", "driver", "admin").
 */
data class UserResponse(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val phoneNumber: String?,
    val role: String
    // Add other fields as per your API response structure
)

/**
 * Represents the driver's current location.
 *
 * @property latitude The latitude of the driver's location.
 * @property longitude The longitude of the driver's location.
 * @property lastUpdated The timestamp when the location was last updated.
 */
data class DriverLocationResponse(
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: String
    // Include additional fields as necessary
)

/**
 * Represents the response received after requesting a ride.
 *
 * @property rideId The unique identifier of the ride.
 * @property status The current status of the ride (e.g., "requested", "in_progress", "completed").
 * @property pickupLocation The pickup location of the ride.
 * @property destinationLocation The destination location of the ride.
 * @property driverId The unique identifier of the assigned driver (nullable).
 */
data class RideResponse(
    val rideId: String,
    val status: String,
    val pickupLocation: String,
    val destinationLocation: String,
    val driverId: String? // Nullable, as a driver might not be assigned yet
    // Add any other relevant fields
) {
    fun toRideDetails(): Nothing {

    }
}

/**
 * Represents detailed information about a ride.
 *
 * @property rideId The unique identifier of the ride.
 * @property pickupLocation The pickup location of the ride.
 * @property destinationLocation The destination location of the ride.
 * @property status The current status of the ride.
 * @property driverId The unique identifier of the assigned driver (nullable).
 */
data class RideInfo(
    val rideId: String,
    val pickupLocation: String,
    val destinationLocation: String,
    val status: String,
    val driverId: String?
    // Add additional properties as needed.
)

/**
 * Represents the response received when fetching the Onfido SDK token.
 *
 * @property token The Onfido SDK token required for initiating KYC processes.
 */
data class SdkTokenResponse(
    val token: String
)

/**
 * API Service interface defining all network endpoints.
 */
interface ApiService {



    /**
     * Logs in a user with the provided email and password.
     *
     * @param loginRequest The login credentials.
     * @return [Response] containing [UserResponse] if successful.
     */
    @POST("users/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<UserResponse>

    /**
     * Requests a ride with specified pickup and destination locations.
     *
     * @param rideInitiateRequest The ride request details.
     * @return [Response] containing [RideResponse] if successful.
     */
    @POST("rides/request")
    suspend fun requestRide(@Body rideInitiateRequest: RideInitiateRequest): Response<RideResponse>

    /**
     * Retrieves the current location of a driver by their ID.
     *
     * @param driverId The unique identifier of the driver.
     * @return [Response] containing [DriverLocationResponse] if successful.
     */
    @GET("drivers/{driverId}/location")
    suspend fun getDriverLocation(@Path("driverId") driverId: String): Response<DriverLocationResponse>

    /**
     * Searches for rides based on pickup and destination locations.
     *
     * @param pickupLocation The pickup location.
     * @param destinationLocation The destination location.
     * @return [Response] containing a list of [RideInfo] if successful.
     */
    @GET("rides/search")
    suspend fun searchRides(
        @Query("pickupLocation") pickupLocation: String,
        @Query("destinationLocation") destinationLocation: String
    ): Response<List<RideInfo>>

    /**
     * Cancels a ride by its ID.
     *
     * @param rideId The unique identifier of the ride to cancel.
     * @return [Response] indicating the success or failure of the operation.
     */
    @POST("rides/cancel/{rideId}")
    suspend fun cancelRide(@Path("rideId") rideId: String): Response<Unit>

    /**
     * Fetches detailed information about a specific ride.
     *
     * @param rideId The unique identifier of the ride.
     * @return [Response] containing [RideResponse] if successful.
     */
    @GET("rides/details/{rideId}")
    suspend fun fetchRideDetails(@Path("rideId") rideId: String): Response<RideResponse>

    /**
     * Fetches the Onfido SDK token required for initiating KYC processes.
     *
     * @return [Response] containing [SdkTokenResponse] if successful.
     */
    @GET("onfido/sdk-token")
    suspend fun getOnfidoSdkToken(): Response<SdkTokenResponse>

    @GET("endpoint")
    suspend fun fetchData(): Response<YourDataModel>

    @POST("endpoint")
    suspend fun sendData(@Body data: YourRequestModel): Response<YourResponseModel>
}
}
