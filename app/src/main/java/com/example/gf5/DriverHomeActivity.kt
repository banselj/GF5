package com.example.gf5

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class DriverHomeActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var rideAssignmentViewModel: RideAssignmentViewModel
    private lateinit var driverStatusViewModel: DriverStatusViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        rideAssignmentViewModel = ViewModelProvider(this)[RideAssignmentViewModel::class.java]
        driverStatusViewModel = ViewModelProvider(this)[DriverStatusViewModel::class.java]

        setContent {
            DriverHomeScreen(
                onLogout = {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                rideAssignmentViewModel = rideAssignmentViewModel,
                driverStatusViewModel = driverStatusViewModel
            )
        }

        startDriverLocationService()
    }

    private fun startDriverLocationService() {
        val intent = Intent(this, DriverLocationService::class.java)
        startService(intent)
    }
}

@Composable
fun DriverHomeScreen(
    onLogout: () -> Unit,
    rideAssignmentViewModel: RideAssignmentViewModel,
    driverStatusViewModel: DriverStatusViewModel
) {
    var currentDestination by remember { mutableStateOf("No destination assigned yet") }

    LaunchedEffect(Unit) {
        rideAssignmentViewModel.currentRide.collect { ride ->
            currentDestination = ride.destinationLocation
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Next Destination: $currentDestination",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            DriverStatusButtons(driverStatusViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text(text = "Logout")
            }
        }
    }
}

@Composable
fun DriverStatusButtons(driverStatusViewModel: DriverStatusViewModel) {
    val status by driverStatusViewModel.status.collectAsState()

    Column {
        Button(
            onClick = { driverStatusViewModel.setStatus(DriverStatus.AVAILABLE) },
            enabled = status != DriverStatus.AVAILABLE
        ) {
            Text(text = "Go Online")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { driverStatusViewModel.setStatus(DriverStatus.BUSY) },
            enabled = status != DriverStatus.BUSY
        ) {
            Text(text = "Go Busy")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { driverStatusViewModel.setStatus(DriverStatus.OFFLINE) },
            enabled = status != DriverStatus.OFFLINE
        ) {
            Text(text = "Go Offline")
        }
    }
}
