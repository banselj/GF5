package com.example.gf5.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gf5.models.User
import com.example.gf5.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user details in [UserDetailsFragment].
 */
class UserDetailsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userDetails = MutableStateFlow<User?>(null)
    val userDetails: StateFlow<User?> get() = _userDetails

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId)
                _userDetails.value = user
            } catch (e: Exception) {
                _error.value = "Failed to fetch user details: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
