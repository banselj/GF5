
package com.example.gf5.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gf5.R
import com.example.gf5.models.DriverStatus
import com.example.gf5.services.DriverLocationService
import com.example.gf5.viewmodels.DriverStatusViewModel
import com.example.gf5.viewmodels.RideAssignmentViewModel
import com.example.gf5.ui.theme.GF5Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@AndroidEntryPoint
class DriverHomeActivity : ComponentActivity() {

    // Inject FirebaseAuth using Hilt
    @Inject
    lateinit var auth: FirebaseAuth

    // Inject ViewModels using Hilt's by viewModels() delegate
    private val rideAssignmentViewModel: RideAssignmentViewModel by viewModels()
    private val driverStatusViewModel: DriverStatusViewModel by viewModels()

    // Activity Result Launcher for handling permission requests
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false
        } else {
            true // Permissions not required for SDK < Q
        }

        if (fineLocationGranted && coarseLocationGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (backgroundLocationGranted) {
                    startDriverLocationService()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.background_location_permission_required),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                startDriverLocationService()
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.location_permissions_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check and request location permissions
        checkAndRequestPermissions()


        setContent {
            GF5Theme {
                // Observe and collect ride assignments and driver status as state
                val currentRide by rideAssignmentViewModel.currentRide.collectAsStateWithLifecycle()
                val driverStatusState by driverStatusViewModel.driverStatusState.collectAsStateWithLifecycle()

                // Map driverStatusState to DriverStatus for UI
                val driverStatus = when (driverStatusState) {
                    is DriverStatusViewModel.DriverStatusState.Success -> {
                        (driverStatusState as DriverStatusViewModel.DriverStatusState.Success).updatedStatus
                    }
                    is DriverStatusViewModel.DriverStatusState.Error -> {
                        // Default or error state, e.g., DriverStatus.IDLE
                        DriverStatus.IDLE
                    }
                    else -> DriverStatus.IDLE // Default state
                }

                DriverHomeScreen(
                    currentDestination = currentRide?.destinationLocation
                        ?: stringResource(R.string.no_destination_assigned),
                    driverStatus = driverStatus,
                    onStatusChange = { newStatus ->
                        driverStatusViewModel.setStatus(newStatus)
                    },
                    onLogout = {
                        handleLogout()
                    }
                )
            }
        }

        // Observe ride assignment changes and display feedback if necessary
        observeRideAssignments()
    }

    /**
     * Checks and requests necessary location permissions.
     */
    private fun checkAndRequestPermissions() {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val backgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            PackageManager.PERMISSION_GRANTED
        }

        val permissionsToRequest = mutableListOf<String>()
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && backgroundLocation != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            locationPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // Permissions already granted
            startDriverLocationService()
        }
    }

    /**
     * Observes ride assignment status and provides real-time updates to the driver.
     */
    private fun observeRideAssignments() {
        lifecycleScope.launch {
            rideAssignmentViewModel.rideAssignmentStatus.collectLatest { status ->
                when (status) {
                    is RideAssignmentViewModel.RideAssignmentStatus.Loading -> {
                        // Show loading indicator if implemented
                    }
                    is RideAssignmentViewModel.RideAssignmentStatus.Success -> {
                        Toast.makeText(this@DriverHomeActivity, getString(R.string.ride_assignment_success), Toast.LENGTH_SHORT).show()
                    }
                    is RideAssignmentViewModel.RideAssignmentStatus.Error -> {
                        Toast.makeText(
                            this@DriverHomeActivity,
                            getString(R.string.ride_assignment_error, status.message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    RideAssignmentViewModel.RideAssignmentStatus.NoRides -> {
                        // Optionally notify the driver that there are no current rides
                        Toast.makeText(
                            this@DriverHomeActivity,
                            getString(R.string.no_current_rides),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    /**
     * Handles user logout by signing out from FirebaseAuth and navigating to LoginActivity.
     */
    private fun handleLogout() {
        // Update driver status to OFFLINE before logging out
        driverStatusViewModel.setStatus(DriverStatus.OFFLINE)

        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    /**
     * Starts the Driver Location Service to track the driver's location in the background.
     */
    private fun startDriverLocationService() {
        val intent = Intent(this, DriverLocationService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}

/**
 * Composable function representing the driver's home screen UI.
 *
 * @param currentDestination The current ride's destination location.
 * @param driverStatus The current status of the driver (e.g., AVAILABLE, BUSY, OFFLINE).
 * @param onStatusChange Callback to change the driver's status.
 * @param onLogout Callback to handle user logout.
 */
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for experimental APIs
@Composable
fun DriverHomeScreen(
    currentDestination: String,
    driverStatus: DriverStatus,
    onStatusChange: (DriverStatus) -> Unit,
    onLogout: () -> Unit
) {
    // Scaffold provides basic material design layout structure
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.driver_home_title)) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = stringResource(R.string.logout)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Display current ride destination
                    RideDestinationCard(currentDestination)

                    // Display driver status controls
                    DriverStatusControls(
                        currentStatus = driverStatus,
                        onStatusChange = onStatusChange
                    )
                }
            }
        }
    )
}

/**
 * Composable function to display the current ride's destination.
 *
 * @param destination The destination location of the current ride.
 */
@Composable
fun RideDestinationCard(destination: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.next_destination),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = destination,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Composable function providing buttons to change the driver's status.
 *
 * @param currentStatus The current status of the driver.
 * @param onStatusChange Callback to change the driver's status.
 */
@Composable
fun DriverStatusControls(
    currentStatus: DriverStatus,
    onStatusChange: (DriverStatus) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.driver_status),
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatusButton(
                text = stringResource(R.string.go_online),
                isSelected = currentStatus == DriverStatus.AVAILABLE,
                onClick = { onStatusChange(DriverStatus.AVAILABLE) }
            )
            StatusButton(
                text = stringResource(R.string.go_busy),
                isSelected = currentStatus == DriverStatus.BUSY,
                onClick = { onStatusChange(DriverStatus.BUSY) }
            )
            StatusButton(
                text = stringResource(R.string.go_offline),
                isSelected = currentStatus == DriverStatus.OFFLINE,
                onClick = { onStatusChange(DriverStatus.OFFLINE) }
            )
        }
    }
}

/**
 * Composable function representing an individual status button.
 *
 * @param text The text to display on the button.
 * @param isSelected Indicates whether the button is currently selected.
 * @param onClick Callback invoked when the button is clicked.
 */
@Composable
fun StatusButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.weight(1f)
    ) {
        Text(text = text)
    }
}
