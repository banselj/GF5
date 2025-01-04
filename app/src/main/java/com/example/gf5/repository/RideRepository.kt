package com.example.gf5.repository

import android.util.Log
import com.example.gf5.models.RideDetails
import com.example.gf5.network.ApiService
import com.example.gf5.network.RideInitiateRequest
import javax.inject.Inject


interface RideRepository {
    suspend fun requestRide(pickupLocation: String, destinationLocation: String): RideDetails
    suspend fun cancelRide(rideId: String): Boolean
    suspend fun fetchRideDetails(rideId: String): RideDetails
}

class RideRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RideRepository {

    override suspend fun requestRide(pickupLocation: String, destinationLocation: String): RideDetails {
        return try {
            val response = apiService.requestRide(RideInitiateRequest(pickupLocation, destinationLocation))
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toRideDetails()
            } else {
                throw Exception("Failed to request ride: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting ride", e)
            throw e
        }
    }

    override suspend fun cancelRide(rideId: String): Boolean {
        return try {
            val response = apiService.cancelRide(rideId)
            if (response.isSuccessful) {
                true
            } else {
                throw Exception("Failed to cancel ride: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error canceling ride", e)
            throw e
        }
    }

    override suspend fun fetchRideDetails(rideId: String): RideDetails {
        return try {
            val response = apiService.fetchRideDetails(rideId)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toRideDetails()
            } else {
                throw Exception("Failed to fetch ride details: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching ride details", e)
            throw e
        }
    }

    companion object {
        private const val TAG = "RideRepository"
    }
}