package com.example.gf5.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gf5.R
import com.example.gf5.models.DriverStatus

/**
 * Composable function representing the driver's home screen UI.
 *
 * @param currentDestination The current ride's destination location.
 * @param driverStatus The current status of the driver (e.g., AVAILABLE, BUSY, OFFLINE).
 * @param onStatusChange Callback to change the driver's status.
 * @param onLogout Callback to handle user logout.
 */
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
