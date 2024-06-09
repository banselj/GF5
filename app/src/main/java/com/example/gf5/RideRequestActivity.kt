package com.example.gf5

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.databinding.ActivityRideRequestBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class RideRequestActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityRideRequestBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val bookingViewModel: BookingViewModel by viewModels()
    private lateinit var currentLocation: LatLng

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val longitude = intent?.getDoubleExtra("longitude", 0.0) ?: 0.0
            currentLocation = LatLng(latitude, longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.requestRideButton.setOnClickListener {
            if (::currentLocation.isInitialized) {
                val destinationLocation = currentLocation.toString()
                if (destinationLocation.isNotEmpty()) {
                    bookingViewModel.submitRideRequest(currentLocation.toString(), destinationLocation)
                } else {
                    Toast.makeText(this, "Please drop a pin for the destination location", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Waiting for location...", Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookingViewModel.bookingStatus.collect { status ->
                    when (status) {
                        is BookingViewModel.BookingStatus.Success -> Toast.makeText(this@RideRequestActivity, "Ride requested successfully", Toast.LENGTH_SHORT).show()
                        is BookingViewModel.BookingStatus.Error -> Toast.makeText(this@RideRequestActivity, "Failed to request ride", Toast.LENGTH_LONG).show()
                        BookingViewModel.BookingStatus.Loading -> {} // Implement loading logic here
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        googleMap.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
            }
        }

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Drop-off Location"))
            currentLocation = latLng
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(locationReceiver, IntentFilter("com.example.gf5.LOCATION_UPDATE"))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(locationReceiver)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
