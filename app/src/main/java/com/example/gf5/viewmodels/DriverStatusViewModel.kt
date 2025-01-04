package com.example.gf5.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.models.DriverStatus
import com.example.gf5.repository.DriverRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DriverStatusViewModel @Inject constructor(
    private val driverRepository: DriverRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _driverStatusState = MutableStateFlow<DriverStatusState>(DriverStatusState.Idle)
    val driverStatusState: StateFlow<DriverStatusState> = _driverStatusState

    fun setStatus(newStatus: DriverStatus) {
        viewModelScope.launch {
            _driverStatusState.value = DriverStatusState.Loading
            try {
                val currentDriverId = getCurrentDriverId()
                val success = driverRepository.updateDriverStatus(currentDriverId, newStatus)
                if (success) {
                    _driverStatusState.value = DriverStatusState.Success(newStatus)
                    Log.d(TAG, "Driver status updated to $newStatus")
                } else {
                    _driverStatusState.value = DriverStatusState.Error("Failed to update driver status.")
                    Log.e(TAG, "Failed to update driver status")
                }
            } catch (e: Exception) {
                _driverStatusState.value = DriverStatusState.Error("Error updating status: ${e.localizedMessage}")
                Log.e(TAG, "Error updating driver status", e)
            }
        }
    }

    private fun getCurrentDriverId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("No authenticated driver found.")
    }

    sealed class DriverStatusState {
        object Idle : DriverStatusState()
        object Loading : DriverStatusState()
        data class Success(val updatedStatus: DriverStatus) : DriverStatusState()
        data class Error(val message: String) : DriverStatusState()
    }

    companion object {
        private const val TAG = "DriverStatusViewModel"
    }
}