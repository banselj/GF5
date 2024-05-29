package com.example.gf5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Empty)
    val authState: StateFlow<AuthState> = _authState

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success(auth.currentUser)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "An unknown error occurred")
                }
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success(auth.currentUser)
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "An unknown error occurred")
                }
            }
        }
    }

    sealed class AuthState {
        data object Loading : AuthState()
        data class Success(val user: FirebaseUser?) : AuthState()
        data class Error(val message: String) : AuthState()
        data object Empty : AuthState()
    }
}
