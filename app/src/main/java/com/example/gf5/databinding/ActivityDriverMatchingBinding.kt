package com.example.gf5.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.gf5.R
import com.google.android.gms.maps.MapView

class ActivityDriverMatchingBinding private constructor(
    private val rootView: View
) : ViewBinding {

    val mapView: MapView = rootView.findViewById(R.id.mapView)
    val cancelButton: View = rootView.findViewById(R.id.cancelButton)

    companion object {
        fun inflate(inflater: LayoutInflater): ActivityDriverMatchingBinding {
            val rootView = inflater.inflate(R.layout.activity_driver_matching, null)
            return ActivityDriverMatchingBinding(rootView)
        }
    }

    override fun getRoot(): View = rootView
}