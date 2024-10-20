package com.example.gf5.repositories

import com.example.gf5.viewmodels.BookingViewModel

interface RideRepository {
    suspend fun requestRide(pickupLocation: String, destinationLocation: String): BookingViewModel.RideDetails
    suspend fun cancelRide(rideId: String): Boolean
    suspend fun fetchRideDetails(rideId: String): BookingViewModel.RideDetails
}

