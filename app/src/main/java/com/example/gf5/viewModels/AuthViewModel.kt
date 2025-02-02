package com.example.gf5.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Empty)
    val authState: StateFlow<AuthState> = _authState

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            if (!isValidEmail(email)) {
                _authState.value = AuthState.Error("Please enter a valid email address.")
                return@launch
            }
            if (!isValidPassword(password)) {
                _authState.value = AuthState.Error("Password must be at least 6 characters long.")
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null && user.isEmailVerified) {
                    _authState.value = AuthState.Success(user)
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                } else if (user != null && !user.isEmailVerified) {
                    _authState.value = AuthState.Error("Please verify your email address.")
                    user.sendEmailVerification().await()
                    _navigationEvent.emit(NavigationEvent.ShowMessage("Verification email sent."))
                } else {
                    _authState.value = AuthState.Error("Authentication failed. Please try again.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login failed. Please check your credentials and try again.")
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            if (!isValidEmail(email)) {
                _authState.value = AuthState.Error("Please enter a valid email address.")
                return@launch
            }
            if (!isValidPassword(password)) {
                _authState.value = AuthState.Error("Password must be at least 6 characters long.")
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                user?.sendEmailVerification()?.await()
                _authState.value = AuthState.Success(user)
                _navigationEvent.emit(NavigationEvent.ShowMessage("Registration successful. Verification email sent."))
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Registration failed. Please try again with a different email.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            auth.signOut()
            _authState.value = AuthState.Empty
            _navigationEvent.emit(NavigationEvent.NavigateToLogin)
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            if (!isValidEmail(email)) {
                _authState.value = AuthState.Error("Please enter a valid email address.")
                return@launch
            }

            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetEmailSent
                _navigationEvent.emit(NavigationEvent.ShowMessage("Password reset email sent."))
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to send password reset email. Please try again.")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    sealed class AuthState {
        object Loading : AuthState()
        data class Success(val user: FirebaseUser?) : AuthState()
        data class Error(val message: String) : AuthState()
        object Empty : AuthState()
        object PasswordResetEmailSent : AuthState()
    }

    sealed class NavigationEvent {
        object NavigateToHome : NavigationEvent()
        object NavigateToLogin : NavigationEvent()
        data class ShowMessage(val message: String) : NavigationEvent()
    }
}