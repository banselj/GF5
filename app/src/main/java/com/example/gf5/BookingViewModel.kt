package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel(private val rideRepository: RideRepository) : ViewModel() {

    private val _bookingStatus = MutableStateFlow<BookingStatus>(BookingStatus.Idle)
    val bookingStatus: StateFlow<BookingStatus> = _bookingStatus

    fun submitRideRequest(pickupLocation: String, destinationLocation: String) {
        viewModelScope.launch {
            _bookingStatus.value = BookingStatus.Loading
            try {
                val rideDetails = rideRepository.requestRide(pickupLocation, destinationLocation)
                _bookingStatus.value = BookingStatus.Success(rideDetails)
            } catch (e: Exception) {
                _bookingStatus.value = BookingStatus.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun cancelRide(rideId: String) {
        viewModelScope.launch {
            _bookingStatus.value = BookingStatus.Loading
            try {
                val success = rideRepository.cancelRide(rideId)
                if (success) {
                    _bookingStatus.value = BookingStatus.Idle
                } else {
                    _bookingStatus.value = BookingStatus.Error("Failed to cancel ride")
                }
            } catch (e: Exception) {
                _bookingStatus.value = BookingStatus.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun fetchRideDetails(rideId: String) {
        viewModelScope.launch {
            _bookingStatus.value = BookingStatus.Loading
            try {
                val rideDetails = rideRepository.fetchRideDetails(rideId)
                _bookingStatus.value = BookingStatus.Success(rideDetails)
            } catch (e: Exception) {
                _bookingStatus.value = BookingStatus.Error(e.message ?: "Unknown Error")
            }
        }
    }

    data class RideDetails(
        val rideId: String,
        val status: String,
        val pickupLocation: String,
        val destinationLocation: String
    )

    sealed class BookingStatus {
        data object Idle : BookingStatus()
        data object Loading : BookingStatus()
        data class Success(val rideDetails: RideDetails) : BookingStatus()
        data class Error(val message: String) : BookingStatus()
    }
}
