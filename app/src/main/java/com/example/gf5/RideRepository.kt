package com.example.gf5

interface RideRepository {
    suspend fun requestRide(pickupLocation: String, destinationLocation: String): BookingViewModel.RideDetails
    suspend fun cancelRide(rideId: String): Boolean
    suspend fun fetchRideDetails(rideId: String): BookingViewModel.RideDetails
}

class RideRepositoryImpl(
    private val apiService: ApiService
) : RideRepository {

    override suspend fun requestRide(pickupLocation: String, destinationLocation: String): BookingViewModel.RideDetails {
        val response = apiService.requestRide(RideRequest(pickupLocation, destinationLocation))
        if (response.isSuccessful && response.body() != null) {
            val rideResponse = response.body()!!
            return BookingViewModel.RideDetails(
                rideId = rideResponse.rideId,
                status = rideResponse.status,
                pickupLocation = rideResponse.pickupLocation,
                destinationLocation = rideResponse.destinationLocation
            )
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

    override suspend fun fetchRideDetails(rideId: String): BookingViewModel.RideDetails {
        val response = apiService.fetchRideDetails(rideId)
        if (response.isSuccessful && response.body() != null) {
            val rideResponse = response.body()!!
            return BookingViewModel.RideDetails(
                rideId = rideResponse.rideId,
                status = rideResponse.status,
                pickupLocation = rideResponse.pickupLocation,
                destinationLocation = rideResponse.destinationLocation
            )
        } else {
            throw Exception("Failed to fetch ride details: ${response.errorBody()?.string()}")
        }
    }
}
