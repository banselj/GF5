package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.*

class MatchingViewModel(private val driverRepository: DriverRepository) : ViewModel() {
    private val _matchingStatus = MutableStateFlow<MatchingStatus>(MatchingStatus.Idle)
    val matchingStatus: StateFlow<MatchingStatus> = _matchingStatus

    /**
     * Requests a match for a ride based on the pickup and destination locations.
     */
    fun requestMatch(pickupLatitude: Double, pickupLongitude: Double, destinationLatitude: Double, destinationLongitude: Double) {
        viewModelScope.launch {
            _matchingStatus.value = MatchingStatus.Searching
            try {
                val availableDrivers = driverRepository.getAvailableDrivers()
                if (availableDrivers.isNotEmpty()) {
                    val matchedDriver = findBestMatch(availableDrivers, pickupLatitude, pickupLongitude)
                    if (matchedDriver != null) {
                        _matchingStatus.value = MatchingStatus.Matched(matchedDriver)
                    } else {
                        _matchingStatus.value = MatchingStatus.NoMatchFound
                    }
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

    /**
     * Finds the best driver match based on rating, proximity, and route.
     */
    private fun findBestMatch(drivers: List<Driver>, pickupLatitude: Double, pickupLongitude: Double): Driver? {
        // Sort drivers by rating (highest first) and proximity (closest first)
        return drivers
            .sortedWith(compareByDescending<Driver> { it.rating }.thenBy { distance(it.latitude, it.longitude, pickupLatitude, pickupLongitude) })
            .firstOrNull()
    }

    /**
     * Calculates the distance between two points using the Haversine formula.
     */
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c // Distance in km
    }

    sealed class MatchingStatus {
        object Idle : MatchingStatus()
        object Searching : MatchingStatus()
        data class Matched(val driver: Driver) : MatchingStatus()
        object NoMatchFound : MatchingStatus()
        data class Error(val message: String) : MatchingStatus()
    }
}
