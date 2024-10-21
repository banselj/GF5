package com.example.gf5.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.models.DriverStatus
import com.example.gf5.repositories.DriverRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing and updating the driver's status.
 *
 * @property driverRepository The repository handling driver-related data operations.
 * @property auth The FirebaseAuth instance for authentication-related operations.
 */
@HiltViewModel
class DriverStatusViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    // Internal mutable state for driver status
    private val _driverStatusState = MutableStateFlow<DriverStatusState>(DriverStatusState.Idle)

    /**
     * Represents the current state of the driver's status.
     * It is exposed as an immutable [StateFlow] to prevent external modifications.
     */
    val driverStatusState: StateFlow<DriverStatusState> = _driverStatusState

    /**
     * Represents the current DriverStatus.
     * Exposed for data binding.
     */
    val currentStatus: DriverStatus?
        get() = when (val state = _driverStatusState.value) {
            is DriverStatusState.Success -> state.updatedStatus
            else -> null
        }

    /**
     * Updates the driver's status to the specified [newStatus].
     *
     * @param newStatus The new status to set for the driver.
     */
    fun setStatus(newStatus: DriverStatus) {
        viewModelScope.launch {
            _driverStatusState.value = DriverStatusState.Loading
            try {
                val currentDriverId = getCurrentDriverId()
                val success = driverRepository.updateDriverStatus(currentDriverId, newStatus)
                if (success) {
                    _driverStatusState.value = DriverStatusState.Success(newStatus)
                } else {
                    _driverStatusState.value = DriverStatusState.Error("Failed to update driver status.")
                }
            } catch (e: Exception) {
                // Optionally log the exception using Timber or another logging library
                _driverStatusState.value = DriverStatusState.Error("Error updating status: ${e.localizedMessage}")
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
        val currentUser = auth.currentUser
        return currentUser?.uid ?: throw IllegalStateException("No authenticated driver found.")
    }

    /**
     * Represents the various states related to driver status updates.
     */
    sealed class DriverStatusState {
        /**
         * Indicates that no action is currently taking place.
         */
        object Idle : DriverStatusState()

        /**
         * Indicates that a driver status update is in progress.
         */
        object Loading : DriverStatusState()

        /**
         * Indicates that the driver status was successfully updated.
         *
         * @property updatedStatus The new status of the driver.
         */
        data class Success(val updatedStatus: DriverStatus) : DriverStatusState()

        /**
         * Indicates that an error occurred while updating the driver status.
         *
         * @property message The error message describing the failure.
         */
        data class Error(val message: String) : DriverStatusState()
    }
}
