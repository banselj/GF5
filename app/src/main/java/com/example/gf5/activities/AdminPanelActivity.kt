package com.example.gf5.activities

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
import com.example.gf5.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

/**
 * Activity representing the Admin Panel where administrators can manage users, rides, and ads.
 */
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

        binding.refreshButton.setOnClickListener {
            refreshData()
        }

        binding.logoutButton.setOnClickListener {
            handleLogout()
        }
    }

    /**
     * Sets up all RecyclerViews with their respective adapters and layout managers.
     */
    private fun setupRecyclerViews() {
        // Set up User RecyclerView
        userAdapter = UserAdapter { user: User ->
            // Handle user item click
            showUserOptions(user)
        }
        binding.usersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminPanelActivity)
            adapter = userAdapter
        }

        // Set up Ride RecyclerView
        rideAdapter = RideAdapter { ride: Ride ->
            // Handle ride item click
            showRideDetails(ride)
        }
        binding.ridesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminPanelActivity)
            adapter = rideAdapter
        }

        // Set up Ad RecyclerView
        adAdapter = AdAdapter { ad: Ad ->
            // Handle ad item click
            showAdDetails(ad)
        }
        binding.adsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminPanelActivity)
            adapter = adAdapter
        }
    }

    /**
     * Observes data from the [AdminViewModel] and updates UI accordingly.
     */
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

    /**
     * Refreshes data by fetching latest users, rides, and ads.
     */
    private fun refreshData() {
        adminViewModel.refreshData()
        Toast.makeText(this, "Data refreshed", Toast.LENGTH_SHORT).show()
    }

    /**
     * Handles user logout by invoking the ViewModel and finishing the activity.
     */
    private fun handleLogout() {
        adminViewModel.logout()
        finish()
    }

    /**
     * Displays options related to the selected user.
     *
     * @param user The user selected from the RecyclerView.
     */
    private fun showUserOptions(user: User) {
        // Implement options like view details, suspend user, etc.
        // For example:
        Toast.makeText(this, "Selected user: ${user.name}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Displays details of the selected ride.
     *
     * @param ride The ride selected from the RecyclerView.
     */
    private fun showRideDetails(ride: Ride) {
        // Implement ride details view
        Toast.makeText(this, "Selected ride ID: ${ride.id}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Displays details of the selected advertisement.
     *
     * @param ad The ad selected from the RecyclerView.
     */
    private fun showAdDetails(ad: Ad) {
        // Implement ad details view
        Toast.makeText(this, "Selected ad campaign: ${ad.campaignName}", Toast.LENGTH_SHORT).show()
    }
}
