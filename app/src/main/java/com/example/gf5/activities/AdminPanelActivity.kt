package com.example.gf5.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gf5.adapters.AdAdapter
import com.example.gf5.adapters.RideAdapter
import com.example.gf5.adapters.UserAdapter
import com.example.gf5.databinding.ActivityAdminPanelBinding
import com.example.gf5.models.Ad
import com.example.gf5.models.Ride
import com.example.gf5.models.User
import com.example.gf5.viewModels.AdminViewModel
import kotlinx.coroutines.launch

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding
    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    private lateinit var rideAdapter: RideAdapter
    private lateinit var adAdapter: AdAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Admin Panel"

        setupRecyclerViews()
        observeData()

        binding.logoutButton.setOnClickListener {
            handleLogout()
        }

        binding.refreshButton.setOnClickListener {
            refreshData()
        }
    }

    private fun setupRecyclerViews() {
        userAdapter = UserAdapter { user: User ->
            showUserOptions(user)
        }
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.usersRecyclerView.adapter = userAdapter

        rideAdapter = RideAdapter { ride: Ride ->
            showRideDetails(ride)
        }
        binding.ridesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.ridesRecyclerView.adapter = rideAdapter

        adAdapter = AdAdapter { ad: Ad ->
            showAdDetails(ad)
        }
        binding.adsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.adsRecyclerView.adapter = adAdapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.userList.collect { users ->
                    if (users.isNotEmpty()) {
                        userAdapter.submitList(users)
                        binding.usersRecyclerView.visibility = View.VISIBLE
                        binding.userEmptyView.visibility = View.GONE
                    } else {
                        binding.usersRecyclerView.visibility = View.GONE
                        binding.userEmptyView.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.rideList.collect { rides ->
                    if (rides.isNotEmpty()) {
                        rideAdapter.submitList(rides)
                        binding.ridesRecyclerView.visibility = View.VISIBLE
                        binding.rideEmptyView.visibility = View.GONE
                    } else {
                        binding.ridesRecyclerView.visibility = View.GONE
                        binding.rideEmptyView.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.adList.collect { ads ->
                    if (ads.isNotEmpty()) {
                        adAdapter.submitList(ads)
                        binding.adsRecyclerView.visibility = View.VISIBLE
                        binding.adEmptyView.visibility = View.GONE
                    } else {
                        binding.adsRecyclerView.visibility = View.GONE
                        binding.adEmptyView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun refreshData() {
        adminViewModel.refreshData()
        Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show()
    }

    private fun handleLogout() {
        adminViewModel.logout()
        finish()
    }

    private fun showUserOptions(user: User) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra("USER_ID", user.id)
        startActivity(intent)
    }

    private fun showRideDetails(ride: Ride) {
        Toast.makeText(this, "Selected ride ID: ${ride.id}", Toast.LENGTH_SHORT).show()
    }

    private fun showAdDetails(ad: Ad) {
        Toast.makeText(this, "Selected ad campaign: ${ad.campaignName}", Toast.LENGTH_SHORT).show()
    }
}