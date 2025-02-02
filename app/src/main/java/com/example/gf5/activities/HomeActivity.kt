package com.example.gf5.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gf5.R
import com.example.gf5.databinding.ActivityHomeBinding
import com.example.gf5.fragments.NotificationsFragment
import com.example.gf5.fragments.ProfileFragment
import com.example.gf5.fragments.RideBookingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ride -> {
                    openFragment(RideBookingFragment())
                    true
                }
                R.id.nav_profile -> {
                    openFragment(ProfileFragment())
                    true
                }
                R.id.nav_notifications -> {
                    openFragment(NotificationsFragment())
                    true
                }
                else -> false
            }
        }

        // Set default selected item
        bottomNavigationView.selectedItemId = R.id.nav_ride
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}