package com.example.gf5.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.gf5.GF5Theme
import com.example.gf5.R
import com.example.gf5.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()



    // Permissions required
    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )

    // Register the permissions callback
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var allGranted = true
        permissions.entries.forEach {
            if (!it.value) {
                allGranted = false
                Log.e(TAG, "Permission denied: ${it.key}")
                Toast.makeText(this, "Permission ${it.key} denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (allGranted) {
            Log.d(TAG, "All permissions granted")
            // Initialize services or proceed with functionality that requires permissions
            mainViewModel.determineNavigation(this)
        } else {
            Toast.makeText(
                this,
                "Required permissions are not granted. The app may not function correctly.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        // Check and request permissions
        if (hasAllPermissions()) {
            mainViewModel.determineNavigation(this)
        } else {
            requestPermissions()
        }

        setContent {
            GF5Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }

        observeNavigationEvents()
    }

    @Composable
    private fun MainContent() {
        var isVisible by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            Log.d(TAG, "LaunchedEffect triggered, delaying for 500ms")
            delay(500) // Delay for animation synchronization
            isVisible = true
            Log.d(TAG, "Animation visibility set to true")
        }

        Box(modifier = Modifier.padding(16.dp)) {
            AnimatedVisibility(visible = isVisible) {
                Log.d(TAG, "AnimatedVisibility is now visible")
                Text(
                    text = stringResource(id = R.string.welcome_message),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }

    private fun observeNavigationEvents() {
        mainViewModel.navigationEvent.observe(this) { event ->
            Log.d(TAG, "Navigation event observed: $event")
            handleNavigationEvent(event)
        }
    }

    private fun handleNavigationEvent(event: MainViewModel.NavigationEvent) {
        Log.d(TAG, "Handling navigation event: $event")
        when (event) {
            is MainViewModel.NavigationEvent.NavigateToRegistration -> {
                navigateToActivity(RegistrationActivity::class.java)
            }
            is MainViewModel.NavigationEvent.NavigateToLogin -> {
                navigateToActivity(LoginActivity::class.java)
            }
            is MainViewModel.NavigationEvent.NavigateToDriverHome -> {
                navigateToActivity(DriverHomeActivity::class.java)
            }
            is MainViewModel.NavigationEvent.NavigateToRiderHome -> {
                navigateToActivity(RiderHomeActivity::class.java)
            }
            is MainViewModel.NavigationEvent.ShowError -> {
                showError(event.message)
            }
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        Log.d(TAG, "Starting activity: ${activityClass.simpleName}")
        startActivity(Intent(this, activityClass))
        finish()
    }

    private fun showError(message: String) {
        Log.e(TAG, "Error: $message")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissionsLauncher.launch(requiredPermissions)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
