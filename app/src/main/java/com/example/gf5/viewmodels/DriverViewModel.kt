package com.example.gf5.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.models.Driver
import com.example.gf5.models.DriverStatus
import com.example.gf5.models.RideDetails
import com.example.gf5.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val repository: DriverRepository
) : ViewModel() {

    private val _availableDrivers = MutableStateFlow<List<Driver>>(emptyList())
    val availableDrivers: StateFlow<List<Driver>> = _availableDrivers

    private val _driverDetails = MutableStateFlow<Driver?>(null)
    val driverDetails: StateFlow<Driver?> = _driverDetails

    private val _rideDetails = MutableStateFlow<RideDetails?>(null)
    val rideDetails: StateFlow<RideDetails?> = _rideDetails

    private val _statusUpdateSuccess = MutableStateFlow<Boolean>(false)
    val statusUpdateSuccess: StateFlow<Boolean> = _statusUpdateSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cancelSearch() {
        viewModelScope.launch {
            try {
                val currentDriverId = getCurrentDriverId()
                val success = repository.updateDriverStatus(currentDriverId, DriverStatus.IDLE)
                _statusUpdateSuccess.value = success
                if (success) {
                    _rideDetails.value = null
                    Log.d(TAG, "Search canceled successfully")
                } else {
                    _error.value = "Failed to cancel search"
                    Log.e(TAG, "Failed to cancel search")
                }
            } catch (e: Exception) {
                _error.value = "Error canceling search: ${e.localizedMessage}"
                Log.e(TAG, "Error canceling search", e)
            }
        }
    }

    fun loadAvailableDrivers() {
        viewModelScope.launch {
            try {
                val drivers = repository.getAvailableDrivers()
                _availableDrivers.value = drivers
                Log.d(TAG, "Loaded ${drivers.size} available drivers")
            } catch (e: Exception) {
                _error.value = "Failed to load available drivers: ${e.localizedMessage}"
                Log.e(TAG, "Error loading available drivers", e)
            }
        }
    }

    fun loadDriverDetails(driverId: String) {
        viewModelScope.launch {
            try {
                val driver = repository.getDriverDetails(driverId)
                _driverDetails.value = driver
                Log.d(TAG, "Loaded details for driver: $driverId")
            } catch (e: Exception) {
                _error.value = "Failed to load driver details: ${e.localizedMessage}"
                Log.e(TAG, "Error loading driver details", e)
            }
        }
    }

    fun updateDriverStatus(driverId: String, newStatus: DriverStatus) {
        viewModelScope.launch {
            try {
                val success = repository.updateDriverStatus(driverId, newStatus)
                _statusUpdateSuccess.value = success
                if (success) {
                    Log.d(TAG, "Driver status updated to $newStatus")
                } else {
                    _error.value = "Failed to update driver status"
                    Log.e(TAG, "Failed to update driver status")
                }
            } catch (e: Exception) {
                _error.value = "Error updating driver status: ${e.localizedMessage}"
                Log.e(TAG, "Error updating driver status", e)
            }
        }
    }

    fun loadNextRide(driverId: String) {
        viewModelScope.launch {
            try {
                val ride = repository.getNextRide(driverId)
                _rideDetails.value = ride
                Log.d(TAG, "Loaded next ride for driver: $driverId")
            } catch (e: Exception) {
                _error.value = "Failed to load next ride: ${e.localizedMessage}"
                Log.e(TAG, "Error loading next ride", e)
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            try {
                repository.logoutDriver()
                Log.d(TAG, "Driver logged out successfully")
            } catch (e: Exception) {
                _error.value = "Error logging out: ${e.localizedMessage}"
                Log.e(TAG, "Error logging out", e)
            }
        }
    }

    private fun getCurrentDriverId(): String {
        return "current_driver_id" // Replace with actual implementation
    }

    companion object {
        private const val TAG = "DriverViewModel"
    }
}