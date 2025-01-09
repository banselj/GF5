package com.example.gf5.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.gf5.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityHomeBinding private constructor(
    private val rootView: View
) : ViewBinding {

    val bottomNavigation: BottomNavigationView
        get() = rootView.findViewById(R.id.bottomNavigation)

    override fun getRoot(): View = rootView

    companion object {
        fun inflate(inflater: LayoutInflater): ActivityHomeBinding {
            val rootView = inflater.inflate(R.layout.activity_home, null, false)
            return ActivityHomeBinding(rootView)
        }
    }
}