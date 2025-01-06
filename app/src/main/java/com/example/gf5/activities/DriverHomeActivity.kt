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
import com.example.gf5.viewModels.DriverStatusViewModel
import com.example.gf5.viewModels.RideAssignmentViewModel
import com.example.gf5.ui.theme.GF5Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@AndroidEntryPoint
class DriverHomeActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val rideAssignmentViewModel: RideAssignmentViewModel by viewModels()
    private val driverStatusViewModel: DriverStatusViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val backgroundLocationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] ?: false
        } else {
            true
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

        checkAndRequestPermissions()

        setContent {
            GF5Theme {
                // Collect StateFlow as Compose state
                val currentRide by rideAssignmentViewModel.currentRide.collectAsStateWithLifecycle()
                val driverStatusState by driverStatusViewModel.driverStatusState.collectAsStateWithLifecycle()

                val driverStatus = when (driverStatusState) {
                    is DriverStatusViewModel.DriverStatusState.Success -> {
                        (driverStatusState as DriverStatusViewModel.DriverStatusState.Success).updatedStatus
                    }
                    is DriverStatusViewModel.DriverStatusState.Error -> {
                        DriverStatus.IDLE
                    }
                    else -> DriverStatus.IDLE
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

        observeRideAssignments()
    }

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
            startDriverLocationService()
        }
    }

    private fun observeRideAssignments() {
        lifecycleScope.launch {
            // Ensure rideAssignmentStatus is a Flow
            rideAssignmentViewModel.rideAssignmentStatus.collect { status ->
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

    private fun handleLogout() {
        driverStatusViewModel.setStatus(DriverStatus.OFFLINE)
        stopService(Intent(this, DriverLocationService::class.java))

        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun startDriverLocationService() {
        val intent = Intent(this, DriverLocationService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DriverHomeScreen(
        currentDestination: String,
        driverStatus: DriverStatus,
        onStatusChange: (DriverStatus) -> Unit,
        onLogout: () -> Unit
    ) {
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
                        RideDestinationCard(currentDestination)
                        DriverStatusControls(
                            currentStatus = driverStatus,
                            onStatusChange = onStatusChange
                        )
                    }
                }
            }
        )
    }

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
                    onClick = { onStatusChange(DriverStatus.AVAILABLE) },
                    modifier = Modifier.weight(1f)
                )
                StatusButton(
                    text = stringResource(R.string.go_busy),
                    isSelected = currentStatus == DriverStatus.BUSY,
                    onClick = { onStatusChange(DriverStatus.BUSY) },
                    modifier = Modifier.weight(1f)
                )
                StatusButton(
                    text = stringResource(R.string.go_offline),
                    isSelected = currentStatus == DriverStatus.OFFLINE,
                    onClick = { onStatusChange(DriverStatus.OFFLINE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun StatusButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = modifier
        ) {
            Text(text = text)
        }
    }
}