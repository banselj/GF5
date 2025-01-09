package com.example.gf5.databinding

import android.view.View
import androidx.viewbinding.ViewBinding
import com.example.gf5.R

class ActivityRideBookingBinding private constructor(
    val root: View,
    val loadingIndicator: View,
    val requestRideButton: View
) : ViewBinding {
    override fun getRoot(): View = root

    companion object {
        fun inflate(layoutInflater: android.view.LayoutInflater): ActivityRideBookingBinding {
            val root = layoutInflater.inflate(R.layout.activity_ride_booking, null)
            return ActivityRideBookingBinding(
                root,
                root.findViewById(R.id.loadingIndicator),
                root.findViewById(R.id.requestRideButton)
            )
        }
    }
}