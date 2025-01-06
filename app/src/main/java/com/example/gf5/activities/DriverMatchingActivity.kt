package com.example.gf5.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.gf5.R
import com.example.gf5.databinding.ActivityDriverMatchingBinding
import com.example.gf5.services.DriverTrackingService
import com.example.gf5.viewModels.DriverStatusViewModel
import com.example.gf5.viewModels.DriverViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DriverMatchingActivity : ComponentActivity() {

    private lateinit var binding: ActivityDriverMatchingBinding

    private val driverViewModel: DriverViewModel by viewModels()
    private val driverStatusViewModel: DriverStatusViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted && coarseLocationGranted) {
            initializeMap()
        } else {
            Toast.makeText(
                this,
                getString(R.string.location_permissions_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DriverMatchingActivity", "onCreate called")
        binding = ActivityDriverMatchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the MapView with the savedInstanceState
        binding.mapView.onCreate(savedInstanceState)

        checkLocationPermissions()

        binding.cancelButton.setOnClickListener {
            Log.d("DriverMatchingActivity", "Cancel button clicked")
            handleCancel()
        }

        observeRideDetails()
        observeDriverStatus()
    }

    private fun checkLocationPermissions() {
        val fineLocation = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocation && coarseLocation) {
            initializeMap()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun initializeMap() {
        binding.mapView.getMapAsync { googleMap ->
            Log.d("DriverMatchingActivity", "Map is ready")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return@getMapAsync
            }
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

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

    private fun observeDriverStatus() {
        Log.d("DriverMatchingActivity", "Observing driver status")
        lifecycleScope.launch {
            driverStatusViewModel.driverStatusState.collectLatest { statusState ->
                Log.d("DriverMatchingActivity", "Driver status updated: $statusState")
                when (statusState) {
                    is DriverStatusViewModel.DriverStatusState.Loading -> {
                        Log.d("DriverMatchingActivity", "DriverStatusState: Loading")
                    }
                    is DriverStatusViewModel.DriverStatusState.Success -> {
                        Log.d("DriverMatchingActivity", "DriverStatusState: Success - ${statusState.updatedStatus}")
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
                    }
                }
            }
        }
    }

    private fun handleCancel() {
        Log.d("DriverMatchingActivity", "Cancelling ride search")
        driverViewModel.cancelSearch()
        stopService(Intent(this, DriverTrackingService::class.java))
        Toast.makeText(this, getString(R.string.search_cancelled), Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }

    private fun navigateToDriverHome() {
        Log.d("DriverMatchingActivity", "Navigating to DriverHomeActivity")
        val intent = Intent(this, DriverHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("DriverMatchingActivity", "onLowMemory called")
        binding.mapView.onLowMemory()
    }
}