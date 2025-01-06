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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.gf5.R
import com.example.gf5.ui.theme.GF5Theme
import com.example.gf5.viewModels.MainViewModel
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
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Log.d(TAG, "All permissions granted")
            mainViewModel.determineNavigation(this)
        } else {
            showError("Required permissions are not granted. The app may not function correctly.")
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
            delay(500) // Delay for animation synchronization
            isVisible = true
        }

        Box(modifier = Modifier.padding(16.dp)) {
            AnimatedVisibility(visible = isVisible) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.welcome_message),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    if (!hasAllPermissions()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { requestPermissions() }) {
                            Text(text = "Grant Permissions")
                        }
                    }
                }
            }
        }
    }

    private fun observeNavigationEvents() {
        mainViewModel.navigationEvent.observe(this, Observer { event ->
            handleNavigationEvent(event)
        })
    }

    private fun handleNavigationEvent(event: MainViewModel.NavigationEvent) {
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
        startActivity(Intent(this, activityClass))
        finish()
    }

    private fun showError(message: String) {
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