package com.example.gf5

import android.app.Application
import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onLoginButtonClick() {
        val emailValue = email.value?.trim()
        val passwordValue = password.value?.trim()

        if (validateForm(emailValue, passwordValue)) {
            loginUser(emailValue!!, passwordValue!!)
        }
    }

    fun onForgotPasswordButtonClick() {
        val emailValue = email.value?.trim()
        if (emailValue.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            Toast.makeText(getApplication(), "Enter a valid email address", Toast.LENGTH_SHORT).show()
        } else {
            resetPassword(emailValue)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(getApplication(), "Login successful", Toast.LENGTH_SHORT).show()
                navigateToRideRequestActivity()
            } else {
                Toast.makeText(getApplication(), "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(getApplication(), "Password reset email sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication(), "Failed to send password reset email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToRideRequestActivity() {
        val intent = Intent(getApplication(), RideRequestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        getApplication<Application>().startActivity(intent)
    }

    private fun validateForm(email: String?, password: String?): Boolean {
        return when {
            email.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(getApplication(), "Enter a valid email address", Toast.LENGTH_SHORT).show()
                false
            }
            password.isNullOrEmpty() || password.length < 6 -> {
                Toast.makeText(getApplication(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}
