package com.example.gf5.services


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.gf5.R
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Service responsible for tracking the driver's location in real-time and updating it to Firestore.
 * Runs as a foreground service to ensure continuous location tracking.
 */
@AndroidEntryPoint
class DriverLocationService : Service() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10_000L // 10 seconds
        fastestInterval = 5_000L // 5 seconds
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "DriverLocationService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "driver_tracking_channel"
    }

    override fun onBind(intent: Intent): IBinder? {
        // This service does not support binding
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // Initialize LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d(TAG, "Location received: $location")
                    serviceScope.launch {
                        updateLocationInFirestore(location)
                    }
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                Log.d(TAG, "Location availability: ${locationAvailability.isLocationAvailable}")
            }
        }

        // Start foreground service with notification
        startForegroundServiceWithNotification()

        // Start location updates
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
            .setOngoing(true) // Makes the notification non-dismissible
            .setPriority(NotificationCompat.PRIORITY_LOW) // Suitable priority for background services

        return builder.build()
    }

    /**
     * Creates a notification channel for Android Oreo and above.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelName = "Driver Tracking Service"
        val channelDescription = "Channel for driver location tracking service."
        val importance = NotificationManager.IMPORTANCE_LOW

        val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
            description = channelDescription
            setSound(null, null) // Disable sound
            enableVibration(false) // Disable vibration
        }

        val notificationManager: NotificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel created")
    }

    /**
     * Starts requesting location updates from FusedLocationProviderClient.
     */
    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                mainLooper
            )
            Log.d(TAG, "Location updates started")
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permissions not granted", e)
            stopSelf()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting location updates", e)
            stopSelf()
        }
    }

    /**
     * Updates the driver's location in Firestore for both 'drivers' and 'vehicles' collections.
     *
     * @param location The current [Location] of the driver.
     */
    private suspend fun updateLocationInFirestore(location: Location) {
        val driverId = getCurrentDriverId()
        if (driverId.isEmpty()) {
            Log.e(TAG, "Driver ID is empty. Cannot update location.")
            return
        }

        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "timestamp" to System.currentTimeMillis()
        )

        try {
            // Create a batch to update both 'drivers' and 'vehicles' collections
            val batch = firestore.batch()
            val driverRef = firestore.collection("drivers").document(driverId)
            val vehicleRef = firestore.collection("vehicles").document(driverId) // Assuming vehicle ID is same as driver ID

            batch.update(driverRef, locationData)
            batch.update(vehicleRef, locationData)

            // Await the batch commit
            batch.commit().await()
            Log.d(TAG, "Batch location update successful")
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Firestore batch update", e)
        }
    }

    /**
     * Retrieves the current authenticated driver's ID.
     *
     * @return The driver's unique identifier or an empty string if not authenticated.
     */
    private fun getCurrentDriverId(): String {
        val currentUser = auth.currentUser
        return currentUser?.uid ?: ""
    }

    /**
     * Stops location updates and cancels the coroutine scope when the service is destroyed.
     */
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
        Log.d(TAG, "DriverLocationService destroyed and coroutine scope cancelled")
    }

    /**
     * Handles low memory situations.
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG, "DriverLocationService received low memory warning")
    }

    /**
     * Callback for when the service is started via [startService].
     *
     * @param intent The [Intent] supplied to [startService].
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's current started state.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "DriverLocationService onStartCommand called")
        // If service is killed by the system, do not recreate it until explicitly started again
        return START_NOT_STICKY
    }
}
