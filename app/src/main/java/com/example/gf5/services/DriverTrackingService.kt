package com.example.gf5.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.gf5.R
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DriverTrackingService : Service() {

    companion object {
        private const val TAG = "DriverTrackingService"
        private const val CHANNEL_ID = "driver_tracking_channel"
        private const val NOTIFICATION_ID = 1
    }

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationRequest: LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10_000L // 10 seconds
    ).setMinUpdateIntervalMillis(5_000L) // 5 seconds
        .build()

    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()

        // Initialize location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Handle location updates (e.g., send to server)
                    Log.d(TAG, "Location update: ${location.latitude}, ${location.longitude}")
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                Log.d(TAG, "Location availability: ${locationAvailability.isLocationAvailable}")
            }
        }

        // Start foreground service with notification
        startForegroundServiceWithNotification()

        // Start requesting location updates
        startLocationUpdates()
    }

    /**
     * Starts the service in the foreground with an ongoing notification.
     */
    private fun startForegroundServiceWithNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Creates a notification for the foreground service.
     */
    private fun createNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.driver_tracking_active))
            .setContentText(getString(R.string.location_tracking_running))
            .setSmallIcon(R.drawable.ic_tracker) // Ensure this icon exists
            .setPriority(NotificationCompat.PRIORITY_LOW) // Suitable priority for background services
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true) // Makes the notification non-dismissible

        return builder.build()
    }

    /**
     * Creates a notification channel for Android O and above.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelName = "Driver Tracking Service"
        val channelDescription = "Tracks driver's location in real-time."
        val importance = NotificationManager.IMPORTANCE_LOW // Low importance for background services

        val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
            description = channelDescription
            setSound(null, null) // Disable sound
            enableVibration(false) // Disable vibration
        }

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    /**
     * Starts requesting location updates.
     */
    private fun startLocationUpdates() {
        // Check for location permissions before requesting updates
        if (hasLocationPermissions()) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "Location permissions not granted.")
                stopSelf() // Stop service if permissions are not granted
                return
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
        } else {
            Log.e(TAG, "Location permissions not granted.")
            stopSelf() // Stop service if permissions are not granted
        }
    }

    /**
     * Checks if the app has necessary location permissions.
     */
    private fun hasLocationPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Background location not required for pre-Android Q
        }

        return fineLocation && coarseLocation && backgroundLocation
    }

    /**
     * Stops requesting location updates when the service is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(TAG, "DriverTrackingService destroyed")
    }

    /**
     * Defines the behavior of the service when it receives a start request.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "DriverTrackingService onStartCommand called")
        // If the service is killed by the system, it won't be recreated until explicitly started again
        return START_NOT_STICKY
    }

    /**
     * This service is not designed to support binding.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
