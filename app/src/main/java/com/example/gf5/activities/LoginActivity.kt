package com.example.gf5.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gf5.R
import com.example.gf5.databinding.ActivityLoginBinding
import com.example.gf5.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupListeners()
        observeViewModel()
    }

    private fun setupUI() {
        Log.d(TAG, "Setting up UI elements")
        listOf(
            binding.emailEditText,
            binding.passwordEditText,
            binding.loginButton,
            binding.forgotPasswordButton
        ).forEach { view ->
            view.animate().alpha(1f).setDuration(1000).start()
        }
    }

    private fun setupListeners() {
        Log.d(TAG, "Setting up listeners for buttons")

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            Log.d(TAG, "Login button clicked with email: $email")
            if (validateForm(email, password)) {
                viewModel.loginUser(email, password)
            }
        }

        binding.forgotPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            Log.d(TAG, "Forgot password button clicked with email: $email")
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Enter a valid email address")
            } else {
                viewModel.resetPassword(email)
            }
        }
    }

    private fun observeViewModel() {
        Log.d(TAG, "Observing ViewModel LiveData")

        viewModel.loginState.observe(this, Observer { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    Log.d(TAG, "Login successful")
                    showToast("Login successful")
                    navigateToRideRequestActivity()
                }
                is LoginViewModel.LoginState.Error -> {
                    Log.e(TAG, "Authentication failed: ${state.message}")
                    showToast("Authentication failed: ${state.message}")
                }
                is LoginViewModel.LoginState.Loading -> {
                    Log.d(TAG, "Login in progress")
                }
            }
        })

        viewModel.resetPasswordState.observe(this, Observer { state ->
            when (state) {
                is LoginViewModel.ResetPasswordState.Success -> {
                    Log.d(TAG, "Password reset email sent successfully")
                    showToast("Password reset email sent")
                }
                is LoginViewModel.ResetPasswordState.Error -> {
                    Log.e(TAG, "Failed to send password reset email: ${state.message}")
                    showToast("Failed to send password reset email: ${state.message}")
                }
                is LoginViewModel.ResetPasswordState.Loading -> {
                    Log.d(TAG, "Sending password reset email")
                }
            }
        })
    }

    private fun navigateToRideRequestActivity() {
        Log.d(TAG, "Navigating to RideRequestActivity")
        val intent = Intent(this, RideRequestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun validateForm(email: String, password: String): Boolean {
        Log.d(TAG, "Validating form with email: $email")
        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Log.w(TAG, "Invalid email address")
                showToast("Enter a valid email address")
                false
            }
            password.isEmpty() || password.length < 6 -> {
                Log.w(TAG, "Invalid password")
                showToast("Password must be at least 6 characters")
                false
            }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Log.d(TAG, "Showing toast message: $message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}