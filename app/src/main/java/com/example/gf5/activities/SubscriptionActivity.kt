package com.example.gf5.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.databinding.ActivitySubscriptionBinding
import com.example.gf5.viewmodels.SubscriptionViewModel
import kotlinx.coroutines.launch

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the action bar title
        supportActionBar?.title = "Subscription Management"

        // Load current subscription details
        loadSubscriptionDetails()

        // Set click listener for upgrade button
        binding.upgradeSubscriptionButton.setOnClickListener {
            handleSubscriptionUpgrade()
        }

        // Set click listener for cancel subscription button
        binding.cancelSubscriptionButton.setOnClickListener {
            handleSubscriptionCancellation()
        }

        // Observe changes to subscription status
        observeSubscriptionStatus()
    }

    private fun loadSubscriptionDetails() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                subscriptionViewModel.subscriptionDetails.collect { details ->
                    if (details != null) {
                        binding.subscriptionStatusText.text = "Status: ${details.status}"
                        binding.subscriptionPriceText.text = "Price: $${details.price}"
                    } else {
                        Toast.makeText(this@SubscriptionActivity, "Failed to load subscription details", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun handleSubscriptionUpgrade() {
        lifecycleScope.launch {
            subscriptionViewModel.upgradeSubscription().collect { result ->
                when (result) {
                    is SubscriptionViewModel.SubscriptionResult.Success -> {
                        Toast.makeText(this@SubscriptionActivity, "Subscription upgraded!", Toast.LENGTH_SHORT).show()
                    }
                    is SubscriptionViewModel.SubscriptionResult.Error -> {
                        Toast.makeText(this@SubscriptionActivity, "Failed to upgrade subscription", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleSubscriptionCancellation() {
        lifecycleScope.launch {
            subscriptionViewModel.cancelSubscription().collect { result ->
                when (result) {
                    is SubscriptionViewModel.SubscriptionResult.Success -> {
                        Toast.makeText(this@SubscriptionActivity, "Subscription cancelled!", Toast.LENGTH_SHORT).show()
                    }
                    is SubscriptionViewModel.SubscriptionResult.Error -> {
                        Toast.makeText(this@SubscriptionActivity, "Failed to cancel subscription", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeSubscriptionStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                subscriptionViewModel.subscriptionStatus.collect { status ->
                    when (status) {
                        is SubscriptionViewModel.SubscriptionStatus.Active -> {
                            binding.subscriptionStatusText.text = "Active Subscription"
                        }
                        is SubscriptionViewModel.SubscriptionStatus.Canceled -> {
                            binding.subscriptionStatusText.text = "Subscription Canceled"
                        }
                        else -> {
                            binding.subscriptionStatusText.text = "Unknown Status"
                        }
                    }
                }
            }
        }
    }
}
