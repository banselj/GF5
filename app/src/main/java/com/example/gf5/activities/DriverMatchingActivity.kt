package com.example.gf5.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gf5.R
import com.example.gf5.databinding.ActivityDriverMatchingBinding
import com.example.gf5.services.DriverTrackingService
import com.example.gf5.viewmodels.DriverStatusViewModel
import com.example.gf5.viewmodels.DriverViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Activity responsible for matching drivers with ride requests.
 * Displays a map and allows drivers to cancel their search.
 */

@AndroidEntryPoint
class DriverMatchingActivity : ComponentActivity() {

    private lateinit var binding: ActivityDriverMatchingBinding

    // Inject the DriverViewModel using Hilt
    private val driverViewModel: DriverViewModel by viewModels()

    // Inject the DriverStatusViewModel using Hilt
    private val driverStatusViewModel: DriverStatusViewModel by viewModels()

    // Activity Result Launcher for handling potential future activity results
    private val someResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("DriverMatchingActivity", "Received activity result: $result")
        // Handle results if necessary
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DriverMatchingActivity", "onCreate called")
        binding = ActivityDriverMatchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize MapView
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { googleMap ->
            Log.d("DriverMatchingActivity", "Map is ready")
            // Initialize your map here
            // Example:
            // googleMap.isMyLocationEnabled = true
            // googleMap.uiSettings.isZoomControlsEnabled = true
        }

        // Setup Cancel button
        binding.cancelButton.setOnClickListener {
            Log.d("DriverMatchingActivity", "Cancel button clicked")
            handleCancel()
        }

        // Observe Ride Details from DriverViewModel
        observeRideDetails()

        // Observe Driver Status from DriverStatusViewModel
        observeDriverStatus()
    }

    /**
     * Observes the [DriverViewModel.rideDetails] to respond to ride assignments.
     */
    private fun observeRideDetails() {
        Log.d("DriverMatchingActivity", "Observing ride details")
        lifecycleScope.launch {
            driverViewModel.rideDetails.collectLatest { rideDetails ->
                rideDetails?.let {
                    Log.d("DriverMatchingActivity", "Ride assigned: $it")
                    Toast.makeText(
                        this@DriverMatchingActivity,
                        getString(R.string.ride_matched),
                        Toast.LENGTH_SHORT
                    ).show()
                    startService(Intent(this@DriverMatchingActivity, DriverTrackingService::class.java))
                    navigateToDriverHome()
                }
            }
        }
    }

    /**
     * Observes the [DriverStatusViewModel.driverStatusState] to respond to status changes.
     */
    private fun observeDriverStatus() {
        Log.d("DriverMatchingActivity", "Observing driver status")
        lifecycleScope.launch {
            driverStatusViewModel.driverStatusState.collectLatest { statusState ->
                Log.d("DriverMatchingActivity", "Driver status updated: $statusState")
                when (statusState) {
                    is DriverStatusViewModel.DriverStatusState.Loading -> {
                        Log.d("DriverMatchingActivity", "DriverStatusState: Loading")
                        // Show loading indicator if implemented
                    }
                    is DriverStatusViewModel.DriverStatusState.Success -> {
                        Log.d("DriverMatchingActivity", "DriverStatusState: Success - ${statusState.updatedStatus}")
                        // Handle successful status update
                    }
                    is DriverStatusViewModel.DriverStatusState.Error -> {
                        Log.e("DriverMatchingActivity", "DriverStatusState: Error - ${statusState.message}")
                        Toast.makeText(
                            this@DriverMatchingActivity,
                            getString(R.string.matching_error, statusState.message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is DriverStatusViewModel.DriverStatusState.Idle -> {
                        Log.d("DriverMatchingActivity", "DriverStatusState: Idle")
                        // Handle idle state if necessary
                    }
                }
            }
        }
    }

    /**
     * Handles the cancellation of the ride search.
     * Stops the [DriverTrackingService] and updates the [ViewModel].
     */
    private fun handleCancel() {
        Log.d("DriverMatchingActivity", "Cancelling ride search")
        driverViewModel.cancelSearch()
        stopService(Intent(this, DriverTrackingService::class.java))
        Toast.makeText(this, getString(R.string.search_cancelled), Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }

    /**
     * Navigates the user to the [DriverHomeActivity].
     */
    private fun navigateToDriverHome() {
        Log.d("DriverMatchingActivity", "Navigating to DriverHomeActivity")
        val intent = Intent(this, DriverHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    /**
     * Navigates the user to the [LoginActivity].
     */
    private fun navigateToLogin() {
        Log.d("DriverMatchingActivity", "Navigating to LoginActivity")
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d("DriverMatchingActivity", "onStart called")
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.d("DriverMatchingActivity", "onResume called")
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("DriverMatchingActivity", "onPause called")
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        Log.d("DriverMatchingActivity", "onStop called")
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DriverMatchingActivity", "onDestroy called")
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("DriverMatchingActivity", "onLowMemory called")
        binding.mapView.onLowMemory()
    }
}
