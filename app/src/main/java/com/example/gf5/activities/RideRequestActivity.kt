package com.example.gf5.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.R
import com.example.gf5.databinding.ActivityRideBookingBinding
import com.example.gf5.viewModels.BookingViewModel
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

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val TAG = "RideRequestActivity"

    private var dropOffLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()

        binding.requestRideButton.setOnClickListener {
            handleRideRequest()
        }

        observeBookingStatus()
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        initializeMap()
    }

    private fun initializeMap() {
        if (checkLocationPermission()) {
            googleMap.isMyLocationEnabled = true
            moveToCurrentLocation()
        } else {
            requestLocationPermission()
        }

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Drop-off Location"))
            dropOffLocation = latLng
        }
    }

    private fun moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    showToast("Unable to retrieve current location")
                }
            }
            .addOnFailureListener { e ->
                showToast("Error retrieving location")
            }
    }

    private fun handleRideRequest() {
        val destination = dropOffLocation
        if (destination != null) {
            val pickupLocation = "current_location"
            val destinationLocation = "${destination.latitude},${destination.longitude}"
            bookingViewModel.submitRideRequest(pickupLocation, destinationLocation)
        } else {
            showToast("Please select a drop-off location on the map")
        }
    }

    private fun observeBookingStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookingViewModel.bookingStatus.collect { status ->
                    when (status) {
                        is BookingViewModel.BookingStatus.Success -> {
                            showToast("Ride requested successfully")
                            navigateToRiderHome()
                        }
                        is BookingViewModel.BookingStatus.Error -> {
                            showToast("Failed to request ride: ${status.message}")
                        }
                        BookingViewModel.BookingStatus.Loading -> {
                            showLoadingIndicator(true)
                        }
                        BookingViewModel.BookingStatus.Idle -> {
                            showLoadingIndicator(false)
                        }
                    }
                }
            }
        }
    }

    private fun navigateToRiderHome() {
        val intent = Intent(this, RiderHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun checkLocationPermission(): Boolean {
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted && coarseLocationGranted
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap.isMyLocationEnabled = true
                    moveToCurrentLocation()
                }
            } else {
                showToast("Location permissions are required to use this feature")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showLoadingIndicator(show: Boolean) {
        binding.loadingIndicator.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}