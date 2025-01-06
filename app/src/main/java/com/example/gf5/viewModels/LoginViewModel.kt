package com.example.gf5.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val userId: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    sealed class ResetPasswordState {
        object Loading : ResetPasswordState()
        object Success : ResetPasswordState()
        data class Error(val message: String) : ResetPasswordState()
    }

    private val _resetPasswordState = MutableLiveData<ResetPasswordState>()
    val resetPasswordState: LiveData<ResetPasswordState> = _resetPasswordState

    fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: "Unknown UID"
                _loginState.postValue(LoginState.Success(userId))
            } catch (e: Exception) {
                _loginState.postValue(LoginState.Error(e.localizedMessage ?: "Authentication failed"))
            }
        }
    }

    fun resetPassword(email: String) {
        _resetPasswordState.value = ResetPasswordState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _resetPasswordState.postValue(ResetPasswordState.Success)
            } catch (e: Exception) {
                _resetPasswordState.postValue(ResetPasswordState.Error(e.localizedMessage ?: "Failed to send password reset email"))
            }
        }
    }
}