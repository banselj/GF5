package com.example.gf5

import com.google.android.gms.maps.OnMapReadyCallback
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gf5.databinding.ActivityDriverMatchingBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import kotlinx.coroutines.launch

class  DriverMatchingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDriverMatchingBinding
    private lateinit var mapView: MapView
    private lateinit var viewModel: MatchingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverMatchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        viewModel = ViewModelProvider(this)[MatchingViewModel::class.java]

        binding.cancelButton.setOnClickListener {
            viewModel.cancelSearch()
            stopService(Intent(this, DriverTrackingService::class.java))
        }

        // Observe matchingStatus using lifecycleScope and collect
        lifecycleScope.launch {
            viewModel.matchingStatus.collect { status ->
                if (status is MatchingViewModel.MatchingStatus.Matched) {
                    startService(Intent(this@DriverMatchingActivity, DriverTrackingService::class.java))
                }
                // Handle other statuses as needed
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        // Initialize your map here
    }

    // Lifecycle methods to manage mapView lifecycle
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
