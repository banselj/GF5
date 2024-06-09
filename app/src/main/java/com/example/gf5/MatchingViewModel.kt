package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.Driver

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
    }
}
