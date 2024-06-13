package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DriverStatusViewModel(private val driverRepository: DriverRepository) : ViewModel() {

    private val _status = MutableStateFlow<DriverStatus>(DriverStatus.OFFLINE)
    val status: StateFlow<DriverStatus> = _status

    fun setStatus(newStatus: DriverStatus) {
        viewModelScope.launch {
            val currentDriverId = getCurrentDriverId() // Implement this method to get current driver ID
            val success = driverRepository.updateDriverStatus(currentDriverId, newStatus)
            if (success) {
                _status.value = newStatus
            } else {
                // Handle the failure case
            }
        }
    }

    private fun getCurrentDriverId(): String {
        // Implement logic to get the current driver's ID
        return "driver123"
    }
}
