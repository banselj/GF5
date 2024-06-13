package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RideAssignmentViewModel(private val driverRepository: DriverRepository) : ViewModel() {

    private val _currentRide = MutableStateFlow<RideDetails>(RideDetails("No ride assigned", "No destination"))
    val currentRide: StateFlow<RideDetails> = _currentRide.asStateFlow()

    init {
        assignNextRide()
    }

    private fun assignNextRide() {
        viewModelScope.launch {
            // Logic to fetch and assign the next ride
            val nextRide = driverRepository.getNextRide() // Implement getNextRide() in DriverRepository
            _currentRide.value = nextRide
        }
    }

    fun onLogout() {
        // Handle logout logic, e.g., clear user data, navigate to login screen
    }

    data class RideDetails(
        val rideId: String,
        val destinationLocation: String
    )
}
