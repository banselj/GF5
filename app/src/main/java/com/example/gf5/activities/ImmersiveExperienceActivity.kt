package com.example.gf5.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gf5.databinding.ActivityImmersiveExperienceBinding
import com.example.gf5.viewmodels.ImmersiveExperienceViewModel
import kotlinx.coroutines.launch

class ImmersiveExperienceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImmersiveExperienceBinding
    private val immersiveViewModel: ImmersiveExperienceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImmersiveExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the action bar title
        supportActionBar?.title = "Immersive Experience"

        // Initialize the immersive experience based on user data
        initializeImmersiveExperience()

        // Observe changes in the immersive environment
        observeImmersiveChanges()
    }

    private fun initializeImmersiveExperience() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                immersiveViewModel.getUserMood().collect { mood ->
                    updateImmersiveEnvironment(mood)
                }
            }
        }
    }

    private fun updateImmersiveEnvironment(mood: String) {
        when (mood) {
            "calm" -> setCalmEnvironment()
            "energetic" -> setEnergeticEnvironment()
            "focused" -> setFocusedEnvironment()
            else -> setDefaultEnvironment()
        }
    }

    private fun setCalmEnvironment() {
        // Apply lighting, sound, and visuals for a calm mood
        binding.immersiveTextView.text = "Setting a calm environment..."
        // Add other UI and sensory updates here (e.g., change background color, play soft music)
    }

    private fun setEnergeticEnvironment() {
        // Apply lighting, sound, and visuals for an energetic mood
        binding.immersiveTextView.text = "Setting an energetic environment!"
        // Add other UI and sensory updates here (e.g., brighter lights, upbeat music)
    }

    private fun setFocusedEnvironment() {
        // Apply lighting, sound, and visuals for a focused mood
        binding.immersiveTextView.text = "Setting a focused environment..."
        // Add other UI and sensory updates here (e.g., dimmed lights, minimal distractions)
    }

    private fun setDefaultEnvironment() {
        // Set a neutral environment when no specific mood is detected
        binding.immersiveTextView.text = "Setting a default environment."
        // Add default UI and sensory updates here
    }

    private fun observeImmersiveChanges() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                immersiveViewModel.immersiveChanges.collect { change ->
                    Toast.makeText(this@ImmersiveExperienceActivity, "Immersive change: $change", Toast.LENGTH_SHORT).show()
                    // Handle real-time adjustments to the immersive environment here
                }
            }
        }
    }
}
