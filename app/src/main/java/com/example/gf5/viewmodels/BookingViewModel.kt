// File: com/example/gf5/model/BookingViewModel.kt
package com.example.gf5.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.repositories.RideRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val rideRepository: RideRepository
) : ViewModel() {

    // Sealed class to represent booking status
    sealed class BookingStatus {
        object Idle : BookingStatus()
        object Loading : BookingStatus()
        object Success : BookingStatus()
        data class Error(val message: String) : BookingStatus()
    }

    // StateFlow for booking status
    private val _bookingStatus = MutableStateFlow<BookingStatus>(BookingStatus.Idle)
    val bookingStatus: StateFlow<BookingStatus> = _bookingStatus

    /**
     * Submits a ride request with the given destination.
     */
    fun submitRideRequest(pickupLocation: String, destinationLocation: String) {
        _bookingStatus.value = BookingStatus.Loading

        viewModelScope.launch {
            try {
                val rideDetails = rideRepository.requestRide(pickupLocation, destinationLocation)
                _bookingStatus.value = BookingStatus.Success
                // Optionally, you can emit the rideDetails if needed
            } catch (e: Exception) {
                _bookingStatus.value = BookingStatus.Error(e.localizedMessage ?: "Failed to request ride")
            }
        }
    }

    /**
     * Cancels a ride with the given ride ID.
     */
    fun cancelRide(rideId: String) {
        _bookingStatus.value = BookingStatus.Loading

        viewModelScope.launch {
            try {
                val isCancelled = rideRepository.cancelRide(rideId)
                if (isCancelled) {
                    _bookingStatus.value = BookingStatus.Success
                } else {
                    _bookingStatus.value = BookingStatus.Error("Failed to cancel ride")
                }
            } catch (e: Exception) {
                _bookingStatus.value = BookingStatus.Error(e.localizedMessage ?: "Failed to cancel ride")
            }
        }
    }

    /**
     * Fetches ride details for the given ride ID.
     */
    fun fetchRideDetails(rideId: String) {
        _bookingStatus.value = BookingStatus.Loading

        viewModelScope.launch {
            try {
                val rideDetails = rideRepository.fetchRideDetails(rideId)
                _bookingStatus.value = BookingStatus.Success
                // Optionally, emit rideDetails
            } catch (e: Exception) {
                _bookingStatus.value = BookingStatus.Error(e.localizedMessage ?: "Failed to fetch ride details")
            }
        }
    }
}
