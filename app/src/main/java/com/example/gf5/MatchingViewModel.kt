package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

enum class DriverStatus {
    AVAILABLE, BUSY, OFFLINE
}

data class Driver(
    val id: String,
    val name: String,
    val location: String,
    val rating: Float,
    val carModel: String,
    val licensePlate: String
)

interface DriverRepository {
    suspend fun getAvailableDrivers(): List<Driver>
    suspend fun getDriverDetails(driverId: String): Driver
    suspend fun updateDriverStatus(driverId: String, newStatus: DriverStatus): Boolean
}

interface DriverService {
    @GET("drivers/available")
    suspend fun getAvailableDrivers(): Response<List<Driver>>

    @GET("drivers/{driverId}")
    suspend fun getDriverDetails(@Path("driverId") driverId: String): Response<Driver>

    @POST("drivers/{driverId}/status")
    suspend fun updateDriverStatus(@Path("driverId") driverId: String, @Body newStatus: DriverStatus): Response<Boolean>
}

class MatchingViewModel(private val driverRepository: DriverRepository) : ViewModel() {
    private val _matchingStatus = MutableStateFlow<MatchingStatus>(MatchingStatus.Idle)
    val matchingStatus: StateFlow<MatchingStatus> = _matchingStatus

    /**
     * Requests a match for a ride based on the pickup and destination locations.
     */
    fun requestMatch(pickupLocation: String, destinationLocation: String) {
        viewModelScope.launch {
            _matchingStatus.value = MatchingStatus.Searching
            try {
                val availableDrivers = driverRepository.getAvailableDrivers()
                if (availableDrivers.isNotEmpty()) {
                    _matchingStatus.value = MatchingStatus.Matched(availableDrivers.first())
                } else {
                    _matchingStatus.value = MatchingStatus.NoMatchFound
                }
            } catch (e: Exception) {
                _matchingStatus.value = MatchingStatus.Error("Failed to find a match: ${e.message}")
            }
        }
    }

    /**
     * Cancels the ongoing search for a match.
     */
    fun cancelSearch() {
        viewModelScope.launch {
            _matchingStatus.value = MatchingStatus.Idle
        }
    }

    sealed class MatchingStatus {
        object Idle : MatchingStatus()
        object Searching : MatchingStatus()
        data class Matched(val driver: Driver) : MatchingStatus()
        object NoMatchFound : MatchingStatus()
        data class Error(val message: String) : MatchingStatus()
        object Loading : MatchingStatus()
    }
}