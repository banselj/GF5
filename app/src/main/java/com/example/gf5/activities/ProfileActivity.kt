// File: com/example/gf5/activities/ProfileActivity.kt
package com.example.gf5.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gf5.databinding.ActivityProfileBinding
import com.example.gf5.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = FirebaseAuth.getInstance().currentUser ?: run {
            navigateToLogin()
            return
        }

        // Observe user profile data
        profileViewModel.userProfile.observe(this, Observer { profile ->
            binding.nameEditText.setText(profile["name"] as? String ?: "")
            binding.emailEditText.setText(currentUser.email ?: "")
        })

        // Observe status messages
        profileViewModel.status.observe(this, Observer { statusMessage ->
            Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        })

        // Load user profile
        binding.progressBar.visibility = View.VISIBLE
        profileViewModel.loadUserProfile(currentUser)

        // Set up button listeners
        binding.saveProfileButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            binding.progressBar.visibility = View.VISIBLE
            profileViewModel.updateUserProfile(currentUser, name, email)
        }

        binding.logoutButton.setOnClickListener {
            profileViewModel.logout()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        // Clear the activity stack to prevent users from returning to ProfileActivity without logging in
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
