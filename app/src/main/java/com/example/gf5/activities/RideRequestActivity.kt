package com.example.gf5.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.R
import com.example.gf5.databinding.ActivityRideBookingBinding
import com.example.gf5.viewmodels.BookingViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.location.FusedLocationProviderClient

@AndroidEntryPoint
class RideRequestActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityRideBookingBinding
    private lateinit var googleMap: GoogleMap

    private val bookingViewModel: BookingViewModel by viewModels()

    // Injected FusedLocationProviderClient
    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    // Logging tag
    private val TAG = "RideRequestActivity"

    private var dropOffLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        binding = ActivityRideBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize and set up the map
        setupMap()

        // Handle request ride button click
        binding.requestRideButton.setOnClickListener {
            handleRideRequest()
        }

        // Observe booking status from ViewModel
        observeBookingStatus()
    }

    /**
     * Sets up the Google Map by initializing the SupportMapFragment.
     */
    private fun setupMap() {
        Log.d(TAG, "Setting up the map")
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Callback when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d(TAG, "Map is ready")
        initializeMap()
    }

    /**
     * Initializes map settings and listeners.
     */
    private fun initializeMap() {
        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true
            moveToCurrentLocation()
        } else {
            requestLocationPermission()
        }

        // Set a listener for map clicks to set drop-off location
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Drop-off Location"))
            dropOffLocation = latLng
            Log.d(TAG, "Drop-off location set to: $latLng")
        }
    }

    /**
     * Moves the camera to the user's current location.
     */
    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    Log.d(TAG, "Moved camera to current location: $currentLatLng")
                } else {
                    Log.w(TAG, "Current location is null")
                    showToast("Unable to retrieve current location")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting last location: ${e.message}")
                showToast("Error retrieving location")
            }
    }

    /**
     * Handles the ride request action.
     */
    private fun handleRideRequest() {
        val destination = dropOffLocation
        if (destination != null) {
            Log.d(TAG, "Submitting ride request to destination: $destination")
            // Convert LatLng to String or another suitable format as per your API
            val pickupLocation = "current_location" // Replace with actual pickup location if available
            val destinationLocation = "${destination.latitude},${destination.longitude}"
            bookingViewModel.submitRideRequest(pickupLocation, destinationLocation)
        } else {
            Log.w(TAG, "Drop-off location not set")
            showToast("Please select a drop-off location on the map")
        }
    }

    /**
     * Observes the booking status from the ViewModel to update the UI accordingly.
     */
    private fun observeBookingStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookingViewModel.bookingStatus.collect { status ->
                    when (status) {
                        is BookingViewModel.BookingStatus.Success -> {
                            Log.d(TAG, "Ride requested successfully")
                            showToast("Ride requested successfully")
                            navigateToRiderHome()
                        }
                        is BookingViewModel.BookingStatus.Error -> {
                            Log.e(TAG, "Failed to request ride: ${status.message}")
                            showToast("Failed to request ride: ${status.message}")
                        }
                        BookingViewModel.BookingStatus.Loading -> {
                            Log.d(TAG, "Submitting ride request...")
                            // Optionally, show a loading indicator here
                            showLoadingIndicator(true)
                        }
                        BookingViewModel.BookingStatus.Idle -> {
                            // Hide loading indicator if necessary
                            showLoadingIndicator(false)
                        }
                        // Handle other states if necessary
                    }
                }
            }
        }
    }

    /**
     * Navigates the user to the RiderHomeActivity after a successful ride request.
     */
    private fun navigateToRiderHome() {
        Log.d(TAG, "Navigating to RiderHomeActivity")
        val intent = Intent(this, RiderHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    /**
     * Checks if location permissions are granted.
     */
    private fun checkLocationPermission(): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted && coarseLocationGranted
    }

    /**
     * Requests location permissions.
     */
    private fun requestLocationPermission() {
        Log.d(TAG, "Requesting location permissions")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Handles the result of the location permission request.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d(TAG, "Location permissions granted")
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap.isMyLocationEnabled = true
                    moveToCurrentLocation()
                }
            } else {
                Log.w(TAG, "Location permissions denied")
                showToast("Location permissions are required to use this feature")
            }
        }
    }

    /**
     * Displays a toast message to the user.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows or hides the loading indicator.
     */
    private fun showLoadingIndicator(show: Boolean) {
        binding.loadingIndicator.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
