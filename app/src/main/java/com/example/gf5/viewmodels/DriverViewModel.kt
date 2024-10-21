package com.example.gf5.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.models.Driver
import com.example.gf5.models.DriverStatus
import com.example.gf5.models.RideDetails
import com.example.gf5.repositories.DriverRepository
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

    /**
     * Cancels the current search for a ride.
     */
    fun cancelSearch() {
        // Implement the logic to cancel the search.
        // This might involve updating the driver's status to IDLE or OFFLINE.
        viewModelScope.launch {
            try {
                // Assuming the repository has a method to cancel search, e.g., updateDriverStatus
                // Here, setting status to IDLE as an example
                val currentDriverId = getCurrentDriverId()
                val success = repository.updateDriverStatus(currentDriverId, DriverStatus.IDLE)
                _statusUpdateSuccess.value = success
                if (success) {
                    // Optionally, clear ride details
                    _rideDetails.value = null
                }
            } catch (e: Exception) {
                // Handle or log the exception as needed
                _statusUpdateSuccess.value = false
            }
        }
    }

    /**
     * Loads the list of available drivers.
     */
    fun loadAvailableDrivers() {
        viewModelScope.launch {
            try {
                val drivers = repository.getAvailableDrivers()
                _availableDrivers.value = drivers
            } catch (e: Exception) {
                // Handle or log the exception as needed
            }
        }
    }

    /**
     * Loads the details of a specific driver.
     *
     * @param driverId The unique identifier of the driver.
     */
    fun loadDriverDetails(driverId: String) {
        viewModelScope.launch {
            try {
                val driver = repository.getDriverDetails(driverId)
                _driverDetails.value = driver
            } catch (e: Exception) {
                // Handle or log the exception as needed
            }
        }
    }

    /**
     * Updates the driver's status.
     *
     * @param driverId The unique identifier of the driver.
     * @param newStatus The new status to set for the driver.
     */
    fun updateDriverStatus(driverId: String, newStatus: DriverStatus) {
        viewModelScope.launch {
            try {
                val success = repository.updateDriverStatus(driverId, newStatus)
                _statusUpdateSuccess.value = success
            } catch (e: Exception) {
                _statusUpdateSuccess.value = false
                // Handle or log the exception as needed
            }
        }
    }

    /**
     * Loads the next ride assigned to the driver.
     *
     * @param driverId The unique identifier of the driver.
     */
    fun loadNextRide(driverId: String) {
        viewModelScope.launch {
            try {
                val ride = repository.getNextRide(driverId)
                _rideDetails.value = ride
            } catch (e: Exception) {
                // Handle or log the exception as needed
            }
        }
    }

    /**
     * Logs out the current driver.
     */
    fun onLogout() {
        // Implement logout logic, such as clearing user data and navigating to login
        // This might involve interacting with FirebaseAuth or other authentication services
        viewModelScope.launch {
            try {
                // Example: Clear user session
                // Assuming repository handles logout
                repository.logoutDriver()
                // Optionally, navigate to login screen
            } catch (e: Exception) {
                // Handle or log the exception as needed
            }
        }
    }

    /**
     * Retrieves the current authenticated driver's ID.
     *
     * @return The driver's unique identifier.
     * @throws IllegalStateException If no driver is currently authenticated.
     */
    private fun getCurrentDriverId(): String {
        // Implement the logic to get current driver ID.
        // This might involve interacting with FirebaseAuth or other authentication services.
        // For example:
        // return auth.currentUser?.uid ?: throw IllegalStateException("No authenticated driver found.")
        return "current_driver_id" // Replace with actual implementation
    }
}
