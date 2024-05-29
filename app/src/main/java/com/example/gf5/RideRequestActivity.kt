package com.example.gf5

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gf5.databinding.ActivityRideRequestBinding
import kotlinx.coroutines.flow.collect

class RideRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRideRequestBinding
    private val bookingViewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRideRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.requestRideButton.setOnClickListener {
            val pickupLocation = binding.pickupLocationEditText.text.toString().trim()
            val destinationLocation = binding.destinationLocationEditText.text.toString().trim()

            if (pickupLocation.isNotEmpty() && destinationLocation.isNotEmpty()) {
                bookingViewModel.submitRideRequest(pickupLocation, destinationLocation)
            } else {
                Toast.makeText(this, "Please fill in both pickup and destination locations", Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launchWhenStarted {
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
