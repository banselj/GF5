package com.example.gf5.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gf5.R
import com.example.gf5.databinding.ActivityHomeBinding
import com.example.gf5.fragments.NotificationsFragment


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setContentView(root: Any) {

    }

    private fun setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
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
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ride -> openFragment(RideBookingFragment())
                R.id.nav_profile -> openFragment(ProfileFragment())
                R.id.nav_notifications -> openFragment(NotificationsFragment())
                else -> false
            }
        }
    }



    private fun openFragment(fragment: NotificationsFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
