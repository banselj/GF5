
package com.example.gf5.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.gf5.databinding.ActivityRegistrationBinding
import com.example.gf5.viewmodels.RegistrationViewModel
import com.onfido.android.sdk.capture.Onfido
import com.onfido.android.sdk.capture.OnfidoConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity responsible for user registration and KYC (Know Your Customer) verification.
 */

@AndroidEntryPoint
class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private val registrationViewModel: RegistrationViewModel by viewModels()

    /**
     * Activity Result Launcher to handle Onfido KYC results.
     */
    private val onfidoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val onfido = Onfido.handleActivityResult(result.resultCode, result.data)
        if (onfido != null) {
            when (onfido.status()) {
                Onfido.Status.FINISHED -> {
                    registrationViewModel.kycState.value = RegistrationViewModel.KYCState.Success
                }
                Onfido.Status.CANCELLED -> {
                    registrationViewModel.kycState.value = RegistrationViewModel.KYCState.Exited("User cancelled KYC process")
                }
                Onfido.Status.ERROR -> {
                    registrationViewModel.kycState.value = RegistrationViewModel.KYCState.Error("KYC failed")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    /**
     * Sets up observers for ViewModel's LiveData.
     */
    private fun setupObservers() {
        registrationViewModel.registrationState.observe(this, Observer { state ->
            when (state) {
                is RegistrationViewModel.RegistrationState.Loading -> {
                    showLoading(true)
                }
                is RegistrationViewModel.RegistrationState.Success -> {
                    showLoading(false)
                    showToast("Registration successful")
                    navigateToRideRequestActivity()
                }
                is RegistrationViewModel.RegistrationState.Error -> {
                    showLoading(false)
                    showToast("Registration failed: ${state.message}")
                }
            }
        })

        registrationViewModel.kycState.observe(this, Observer { state ->
            when (state) {
                is RegistrationViewModel.KYCState.Loading -> {
                    showLoading(true)
                }
                is RegistrationViewModel.KYCState.Initiate -> {
                    showLoading(false)
                    launchKYC(state.config)
                }
                is RegistrationViewModel.KYCState.Success -> {
                    showLoading(false)
                    showToast("KYC verification successful")
                    // Proceed to next step, e.g., navigate to Ride Request Activity
                }
                is RegistrationViewModel.KYCState.Error -> {
                    showLoading(false)
                    showToast(state.message)
                }
                is RegistrationViewModel.KYCState.Exited -> {
                    showLoading(false)
                    showToast(state.reason)
                }
            }
        })
    }

    /**
     * Sets up click listeners for UI elements.
     */
    private fun setupListeners() {
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                registrationViewModel.registerUser(email, password)
            }
        }

        binding.verifyIDButton.setOnClickListener {
            initiateKYCProcess()
        }
    }

    /**
     * Validates user input for email and password.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return `true` if input is valid, `false` otherwise.
     */
    private fun validateInput(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Enter a valid email address")
                false
            }
            password.isEmpty() || !password.matches(PASSWORD_REGEX.toRegex()) -> {
                showToast("Password must be at least 8 characters and include a number, an uppercase letter, a lowercase letter, and a special character")
                false
            }
            else -> true
        }
    }

    /**
     * Initiates the KYC process by fetching the SDK token and configuring Onfido.
     */
    private fun initiateKYCProcess() {
        // Show loading indicator
        showLoading(true)
        // Fetch SDK token from ViewModel or Repository
        registrationViewModel.viewModelScope.launch {
            try {
                val sdkToken = registrationViewModel.onfidoRepository.fetchSdkToken()
                registrationViewModel.startKYC(sdkToken)
            } catch (e: Exception) {
                showLoading(false)
                showToast("Failed to initiate KYC: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Launches the KYC process using Onfido SDK.
     *
     * @param config The Onfido configuration.
     */
    private fun launchKYC(config: OnfidoConfig) {
        try {
            val intent = Onfido.startActivityForResult(this, config)
            onfidoResultLauncher.launch(intent)
        } catch (e: Exception) {
            showToast("Error starting KYC process: ${e.message}")
        }
    }

    /**
     * Displays or hides the loading indicator.
     *
     * @param isLoading `true` to show loading, `false` to hide.
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !isLoading
        binding.verifyIDButton.isEnabled = !isLoading
    }

    /**
     * Shows a Toast message to the user.
     *
     * @param message The message to display.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Navigates to the Ride Request Activity.
     */
    private fun navigateToRideRequestActivity() {
        val intent = Intent(this, RideRequestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    companion object {
        private const val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$"
    }
}
