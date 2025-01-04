package com.example.gf5.repository


import com.example.gf5.models.RideDetails
import com.example.gf5.network.ApiService
import com.example.gf5.network.RideRequest
import com.example.gf5.network.RideResponse
import javax.inject.Inject

class RideRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RideRepository {

    override suspend fun requestRide(pickupLocation: String, destinationLocation: String): RideDetails {
        val response = apiService.requestRide(RideRequest(pickupLocation, destinationLocation))
        if (response.isSuccessful && response.body() != null) {
            val rideResponse: RideResponse = response.body()!!
            return rideResponse.toRideDetails()
        } else {
            throw Exception("Failed to request ride: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun cancelRide(rideId: String): Boolean {
        val response = apiService.cancelRide(rideId)
        if (response.isSuccessful) {
            return true
        } else {
            throw Exception("Failed to cancel ride: ${response.errorBody()?.string()}")
        }
    }

    override suspend fun fetchRideDetails(rideId: String): RideDetails {
        val response = apiService.fetchRideDetails(rideId)
        if (response.isSuccessful && response.body() != null) {
            val rideResponse: RideResponse = response.body()!!
            return rideResponse.toRideDetails()
        } else {
            throw Exception("Failed to fetch ride details: ${response.errorBody()?.string()}")
        }
    }

    // Extension function to map RideResponse to RideDetails
    private fun RideResponse.toRideDetails(): RideDetails {
        return RideDetails(
            rideId = this.rideId,
            status = this.status,
            pickupLocation = this.pickupLocation,
            destinationLocation = this.destinationLocation
        )
    }
}
