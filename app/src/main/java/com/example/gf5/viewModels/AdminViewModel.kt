package com.example.gf5.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.models.Ad
import com.example.gf5.models.Ride
import com.example.gf5.models.User
import com.example.gf5.repository.UserRepository
import com.example.gf5.repository.RideRepository
import com.example.gf5.repository.AdRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val userRepository: UserRepository,
    private val rideRepository: RideRepository,
    private val adRepository: AdRepository
) : ViewModel() {

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> get() = _userList

    private val _rideList = MutableStateFlow<List<Ride>>(emptyList())
    val rideList: StateFlow<List<Ride>> get() = _rideList

    private val _adList = MutableStateFlow<List<Ad>>(emptyList())
    val adList: StateFlow<List<Ad>> get() = _adList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun refreshData() {
        viewModelScope.launch {
            try {
                val users = userRepository.getAllUsers()
                _userList.value = users

                val rides = rideRepository.getAllRides()
                _rideList.value = rides

                val ads = adRepository.getAllAds()
                _adList.value = ads
            } catch (e: Exception) {
                _error.value = "Failed to refresh data: ${e.message}"
            }
        }
    }

    fun logout() {
        // Implement logout logic, e.g., clearing user session
    }

    fun clearError() {
        _error.value = null
    }
}